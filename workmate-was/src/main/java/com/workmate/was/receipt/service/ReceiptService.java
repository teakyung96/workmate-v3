package com.workmate.was.receipt.service;

import com.workmate.was.receipt.vo.Receipt;
import com.workmate.was.receipt.vo.ReceiptAnalysisResponseVo;
import com.workmate.was.receipt.vo.ReceiptSaveRequestVo;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

/** 영수증 비즈니스 로직 처리 서비스 인터페이스. */
public interface ReceiptService {

    ReceiptAnalysisResponseVo analyzeUploadedReceipt(MultipartFile file) throws IOException;

    Receipt saveConfirmedReceipt(Long userSeq, ReceiptSaveRequestVo request);

    List<Receipt> getReceiptHistory(Long userSeq);

    byte[] exportReceiptHistoryToCsv(Long userSeq);
}
