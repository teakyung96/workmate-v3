package com.workmate.was.receipt.vo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자가 영수증 확인 폼에서 최종 검증 및 수정한 후 저장을 요청하는 VO.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptSaveRequestVo {

    /** 임시 업로드된 이미지 파일 경로 */
    @NotBlank(message = "이미지 경로가 누락되었습니다.")
    private String imagePath;

    /** 최종 확정 결제 금액 */
    @NotNull(message = "결제 금액은 필수 입력 항목입니다.")
    @Min(value = 0, message = "금액은 0원 이상이어야 합니다.")
    private Integer payAmount;

    /** 최종 확정 사업자등록번호 (10자리 숫자만) */
    @NotBlank(message = "사업자등록번호는 필수 입력 항목입니다.")
    @Pattern(regexp = "\\d{10}", message = "사업자등록번호는 하이픈 없이 10자리 숫자여야 합니다.")
    private String bizNo;

    /** 최종 확정 결제일 (YYYYMMDD 형식) */
    @NotBlank(message = "결제일은 필수 입력 항목입니다.")
    @Pattern(regexp = "\\d{8}", message = "결제일은 YYYYMMDD 형식의 8자리 숫자여야 합니다.")
    private String payDate;

    /** 선택된 카드사명 */
    private String cardName;

    /** 카드 선택 방식 ('AUTO' | 'MANUAL') */
    @NotBlank(message = "카드 선택 방식이 누락되었습니다.")
    private String selectType;

    /** AI가 최초에 추출했던 분석 원본 JSON 데이터 */
    private String rawJson;
}
