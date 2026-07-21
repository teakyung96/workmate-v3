package com.workmate.was.ocr.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** AI가 영수증 이미지에서 추출한 결제 건 정보 VO. */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OcrResultVo {
    private String cardName;
    private Integer payAmount;
    private String payDate;
    private String bizNo;
}
