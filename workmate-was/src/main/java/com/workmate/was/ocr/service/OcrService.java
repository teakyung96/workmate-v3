package com.workmate.was.ocr.service;

import com.workmate.was.ocr.vo.OcrResultVo;
import org.springframework.core.io.Resource;
import java.util.List;

/** Spring AI (Gemini) 기반 OCR 서비스 인터페이스. */
public interface OcrService {

    List<OcrResultVo> analyzeReceipt(Resource imageResource, String mimeType);

    String toJsonString(List<OcrResultVo> results);
}
