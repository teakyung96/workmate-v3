package com.workmate.was.receipt.tool;

import com.workmate.was.receipt.dao.ReceiptRepository;
import com.workmate.was.receipt.vo.Receipt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 채팅 AI 가 호출하는 영수증 조회 도구 (F5-01).
 * 사용자 식별자는 ToolContext(로그인 사용자)에서 받아 <b>본인 데이터만</b> 조회한다 (F5-03).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReceiptTools {

    private final ReceiptRepository receiptRepository;

    /**
     * 특정 연/월의 영수증 합계·건수를 조회한다. "지난달 영수증 총액?" 류 질문에 AI 가 호출한다.
     *
     * @param year        조회 연도
     * @param month       조회 월 (1~12)
     * @param toolContext 로그인 사용자(userSeq) 컨텍스트
     * @return 사람이 읽을 수 있는 요약 문자열
     */
    @Tool(description = "특정 연도와 월의 영수증 결제 합계 금액과 건수를 조회한다. "
            + "'지난달 영수증 총액', '이번달 지출' 등 사용자의 영수증 지출을 물을 때 사용한다.")
    public String getMonthlyReceiptSummary(
            @ToolParam(description = "조회 연도 4자리 (예: 2026)") int year,
            @ToolParam(description = "조회 월 1~12") int month,
            ToolContext toolContext) {

        Long userSeq = (Long) toolContext.getContext().get("userSeq");
        String start = String.format("%04d%02d01", year, month);
        String end = String.format("%04d%02d31", year, month);

        List<Receipt> receipts = receiptRepository.findByUserSeqAndPayDateBetween(userSeq, start, end);
        long total = receipts.stream().mapToLong(r -> r.getPayAmount() == null ? 0 : r.getPayAmount()).sum();

        log.info("[Tool] getMonthlyReceiptSummary - userSeq: {}, {}-{}, count: {}, total: {}",
                userSeq, year, month, receipts.size(), total);

        if (receipts.isEmpty()) {
            return String.format("%d년 %d월에는 등록된 영수증 내역이 없습니다.", year, month);
        }
        return String.format("%d년 %d월 영수증: 총 %d건, 합계 %,d원", year, month, receipts.size(), total);
    }
}
