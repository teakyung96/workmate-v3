package com.workmate.was.guide.service.impl;

import com.workmate.was.guide.dao.GuideRepository;
import com.workmate.was.guide.service.GuideService;
import com.workmate.was.guide.vo.Guide;
import com.workmate.was.guide.vo.GuideResponseVo;
import com.workmate.was.guide.vo.GuideSaveRequestVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 가이드 문서 처리 및 벡터 스토어 RAG 적재 서비스 구현체.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GuideServiceImpl implements GuideService {

    private final GuideRepository guideRepository;
    private final VectorStore vectorStore;
    private final JdbcTemplate jdbcTemplate;

    /**
     * 새로운 가이드 문서를 등록하고 본문을 청크 분할하여 벡터 스토어에 적재한다.
     *
     * @param userSeq 작성자 식별자
     * @param request 등록 요청 정보
     * @return 등록 완료된 가이드 문서 정보 VO
     */
    @Override
    @Transactional
    public GuideResponseVo createGuide(Long userSeq, GuideSaveRequestVo request) {
        log.info("가이드 문서 등록 요청. Title: {}, UserSeq: {}", request.getTitle(), userSeq);

        Guide guide = Guide.builder()
                .userSeq(userSeq)
                .title(request.getTitle())
                .content(request.getContent())
                .isPublic(request.getIsPublic())
                .build();

        Guide saved = guideRepository.save(guide);

        // 벡터 스토어 임베딩 적재
        saveEmbeddings(saved);

        return new GuideResponseVo(saved);
    }

    /**
     * 기존 가이드 문서를 수정하고, 벡터 스토어의 기존 임베딩을 지운 뒤 새로 적재한다.
     *
     * @param guideSeq 가이드 문서 식별자
     * @param request 수정 요청 정보
     * @return 수정 완료된 가이드 문서 정보 VO
     */
    @Override
    @Transactional
    public GuideResponseVo updateGuide(Long userSeq, Long guideSeq, GuideSaveRequestVo request) {
        log.info("가이드 문서 수정 요청. GuideSeq: {}, UserSeq: {}", guideSeq, userSeq);

        Guide guide = findOwnedGuide(userSeq, guideSeq);

        guide.update(request.getTitle(), request.getContent(), request.getIsPublic());

        // 기존 임베딩 데이터 제거
        deleteEmbeddings(guideSeq);

        // 새로운 임베딩 데이터 적재
        saveEmbeddings(guide);

        return new GuideResponseVo(guide);
    }

    /**
     * 가이드 문서를 삭제하고 벡터 스토어에 적재된 관련 임베딩 청크들도 모두 삭제한다.
     *
     * @param guideSeq 가이드 문서 식별자
     */
    @Override
    @Transactional
    public void deleteGuide(Long userSeq, Long guideSeq) {
        log.info("가이드 문서 삭제 요청. GuideSeq: {}, UserSeq: {}", guideSeq, userSeq);

        Guide guide = findOwnedGuide(userSeq, guideSeq);

        // 벡터 스토어 임베딩 데이터 제거
        deleteEmbeddings(guideSeq);

        // 엔티티 삭제
        guideRepository.delete(guide);
    }

    /**
     * 가이드 문서 상세 단건 조회.
     *
     * @param guideSeq 가이드 문서 식별자
     * @return 가이드 문서 정보 VO
     */
    @Override
    @Transactional(readOnly = true)
    public GuideResponseVo getGuide(Long userSeq, Long guideSeq) {
        Guide guide = guideRepository.findById(guideSeq)
                .orElseThrow(() -> new IllegalArgumentException("해당 가이드 문서를 찾을 수 없습니다. ID: " + guideSeq));
        // 접근 제어: 비공개 문서는 본인만 열람 가능 (F4-08)
        if (!Boolean.TRUE.equals(guide.getIsPublic()) && !guide.getUserSeq().equals(userSeq)) {
            throw new IllegalArgumentException("해당 가이드 문서에 접근할 수 없습니다.");
        }
        return new GuideResponseVo(guide);
    }

    /**
     * 특정 사용자가 등록한 가이드 목록을 조회한다.
     *
     * @param userSeq 사용자 식별자
     * @return 가이드 목록 VO 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public List<GuideResponseVo> getAccessibleGuides(Long userSeq) {
        // 본인 문서 + 공개 문서 병합(중복 제거) 후 최신순 (G1, F4-08)
        Map<Long, Guide> merged = new java.util.LinkedHashMap<>();
        guideRepository.findByUserSeqOrderByCreatedAtDesc(userSeq)
                .forEach(g -> merged.put(g.getGuideSeq(), g));
        guideRepository.findByIsPublicOrderByCreatedAtDesc(true)
                .forEach(g -> merged.putIfAbsent(g.getGuideSeq(), g));
        return merged.values().stream()
                .sorted(java.util.Comparator.comparing(Guide::getCreatedAt).reversed())
                .map(GuideResponseVo::new)
                .collect(Collectors.toList());
    }

    /** 본인 소유 가이드를 조회한다. 없거나 타인 문서면 예외 (F4-01) */
    private Guide findOwnedGuide(Long userSeq, Long guideSeq) {
        Guide guide = guideRepository.findById(guideSeq)
                .orElseThrow(() -> new IllegalArgumentException("해당 가이드 문서를 찾을 수 없습니다. ID: " + guideSeq));
        if (!guide.getUserSeq().equals(userSeq)) {
            throw new IllegalArgumentException("본인의 가이드 문서만 수정·삭제할 수 있습니다.");
        }
        return guide;
    }

    /**
     * 가이드 문서를 청크로 쪼개어 임베딩을 생성한 후 벡터 스토어에 저장한다.
     */
    private void saveEmbeddings(Guide guide) {
        try {
            log.info("가이드 문서 본문 청크 분할 및 임베딩 생성 시작 (GuideSeq: {})", guide.getGuideSeq());

            // 기본 설정의 토큰 기반 텍스트 스플리터 생성
            TokenTextSplitter splitter = new TokenTextSplitter();

            // 문서 본문 생성 및 메타데이터 추가 (userSeq 는 RAG 검색 시 본인·공개 필터에 사용, F4-08)
            Document doc = new Document(guide.getContent(), Map.of(
                    "guideSeq", guide.getGuideSeq(),
                    "userSeq", guide.getUserSeq(),
                    "title", guide.getTitle(),
                    "isPublic", guide.getIsPublic()
            ));

            List<Document> chunks = splitter.split(List.of(doc));
            log.info("문서 분할 완료. 생성된 청크 수: {}개", chunks.size());

            // 벡터 스토어에 적재 (EmbeddingModel 호출 및 DB 저장 자동 수행)
            vectorStore.add(chunks);
            log.info("벡터 스토어 임베딩 적재 완료. (GuideSeq: {})", guide.getGuideSeq());

        } catch (Exception e) {
            log.error("벡터 스토어 임베딩 적재 실패 (GuideSeq: {}): {}", guide.getGuideSeq(), e.getMessage(), e);
            // 비즈니스 트랜잭션 전체가 롤백되지 않도록 선택할 수도 있으나,
            // 지식 검색의 데이터 정합성을 위해 런타임 예외로 위임하여 롤백 처리
            throw new RuntimeException("RAG 벡터 DB 적재에 실패했습니다. 가이드 문서 저장을 취소합니다.", e);
        }
    }

    /**
     * 벡터 스토어 테이블에서 특정 가이드 문서에 속한 모든 청크를 직접 제거한다.
     */
    private void deleteEmbeddings(Long guideSeq) {
        try {
            log.info("벡터 스토어 내 기존 청크 삭제 시작 (GuideSeq: {})", guideSeq);
            // jsonb 타입의 metadata 필드 내 guideSeq 값을 참조하여 매칭되는 레코드 제거
            String sql = "DELETE FROM vector_store WHERE (metadata->>'guideSeq')::bigint = ?";
            int rows = jdbcTemplate.update(sql, guideSeq);
            log.info("벡터 스토어 기존 청크 삭제 완료. 삭제된 청크 수: {}개", rows);
        } catch (Exception e) {
            log.error("벡터 스토어 청크 삭제 실패 (GuideSeq: {}): {}", guideSeq, e.getMessage(), e);
            throw new RuntimeException("RAG 벡터 DB 기존 데이터 갱신에 실패했습니다.", e);
        }
    }
}
