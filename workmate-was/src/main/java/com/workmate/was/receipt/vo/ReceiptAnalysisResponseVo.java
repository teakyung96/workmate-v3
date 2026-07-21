package com.workmate.was.receipt.vo;

import com.workmate.was.ocr.vo.OcrResultVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

/** 영수증 이미지 분석 결과 VO. */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptAnalysisResponseVo {
    private String imagePath;
    private Integer payAmount;
    private String bizNo;
    private String payDate;
    private String cardName;
    private Boolean bizNoValid;
    private String selectType;
    private String rawJson;
    private List<OcrResultVo> items;
}
