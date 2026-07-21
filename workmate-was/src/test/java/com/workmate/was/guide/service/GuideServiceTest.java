package com.workmate.was.guide.service;

import com.workmate.was.guide.dao.GuideRepository;
import com.workmate.was.guide.vo.GuideResponseVo;
import com.workmate.was.guide.vo.GuideSaveRequestVo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.Embedding;

// GEMINI_API_KEY 환경변수가 없어도 컨텍스트가 뜨도록 더미 키를 주입한다
// (Google GenAI 자동설정이 api-key 부재 시 빈 생성 단계에서 즉시 실패하기 때문)
@SpringBootTest(properties = "spring.ai.google.genai.api-key=dummy-key-for-test")
@Import(GuideServiceTest.TestConfig.class)
class GuideServiceTest {

    @TestConfiguration
    static class TestConfig {
        /**
         * 실제 임베딩 API 호출을 대체하는 가짜 임베딩 모델 빈.
         * 자동설정이 만드는 Google GenAI 임베딩 빈과 중복되므로 @Primary로 우선순위를 준다.
         */
        @Bean
        @org.springframework.context.annotation.Primary
        public EmbeddingModel embeddingModel() {
            return new FakeEmbeddingModel();
        }
    }

    /**
     * API 키 없는 로컬 환경에서도 RAG 적재/검색 통합 테스트가 돌도록 하는 가짜 임베딩 모델.
     * embed(List)류 메서드는 인터페이스 default 구현이 call()로 위임하므로 여기서 재정의하지 않는다.
     * (List&lt;Document&gt; 오버로드를 직접 선언하면 embed(List&lt;String&gt;)와 제네릭 소거 충돌로 컴파일 실패)
     */
    static class FakeEmbeddingModel implements EmbeddingModel {

        /** pgvector 코사인 거리 연산의 분모 0 오류를 막기 위해 첫 차원에 크기를 준 고정 벡터를 만든다. */
        private static float[] fakeVector() {
            float[] vector = new float[768];
            vector[0] = 1.0f;
            return vector;
        }

        @Override
        public EmbeddingResponse call(EmbeddingRequest request) {
            List<Embedding> embeddings = java.util.stream.IntStream.range(0, request.getInstructions().size())
                    .mapToObj(i -> new Embedding(fakeVector(), i))
                    .collect(java.util.stream.Collectors.toList());
            return new EmbeddingResponse(embeddings);
        }

        @Override
        public float[] embed(org.springframework.ai.document.Document document) {
            return fakeVector();
        }

        @Override
        public int dimensions() {
            return 768;
        }
    }

    @Autowired
    private GuideService guideService;

    @Autowired
    private GuideRepository guideRepository;

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void tearDown() {
        // 테스트용 데이터 클렌징
        guideRepository.deleteAll();
        jdbcTemplate.execute("DELETE FROM vector_store");
    }

    @Test
    @DisplayName("가이드 문서를 저장하면 본문이 분할되어 벡터 스토어에 적재되며 유사도 검색이 가능해야 한다")
    void createGuideAndSearchSimilarity() {
        // given
        Long userSeq = 1L;
        GuideSaveRequestVo request = GuideSaveRequestVo.builder()
                .title("Workmate 사내 경비 처리 규칙")
                .content("롯데법인카드 결제 한도는 10만원입니다. 초과 지출 시 증빙 자료를 반드시 첨부해 주세요.")
                .isPublic(true)
                .build();

        // when (등록 처리 -> 내부적으로 TokenTextSplitter 분할 및 벡터 DB 저장 작동)
        GuideResponseVo response = guideService.createGuide(userSeq, request);
        assertThat(response.getGuideSeq()).isNotNull();

        // 가이드 원본과 벡터 청크가 실제로 DB에 적재되었는지 확인
        int guideCount = jdbcTemplate.queryForObject("SELECT count(*) FROM guide", Integer.class);
        int vectorCount = jdbcTemplate.queryForObject("SELECT count(*) FROM vector_store", Integer.class);
        assertThat(guideCount).isEqualTo(1);
        assertThat(vectorCount).isGreaterThanOrEqualTo(1);

        // then (유사도 검색 검증)
        String query = "롯데카드 결제 한도";
        List<Document> searchResults = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(2)
                        .build()
        );

        // 검색된 문서 청크들 중 방금 등록한 문서가 포함되어 있는지 확인
        assertThat(searchResults).isNotEmpty();
        Document matchedDoc = searchResults.get(0);
        assertThat(matchedDoc.getText()).contains("롯데법인카드", "10만원");
        // JSON parsing으로 리포트될 때 정수 타입이 Integer가 될 수 있으므로 문자열 변환 비교
        assertThat(matchedDoc.getMetadata().get("guideSeq").toString()).isEqualTo(response.getGuideSeq().toString());
        assertThat(matchedDoc.getMetadata().get("title")).isEqualTo("Workmate 사내 경비 처리 규칙");
    }
}
