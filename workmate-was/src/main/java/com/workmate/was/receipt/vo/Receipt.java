package com.workmate.was.receipt.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * 영수증 정보 엔티티.
 * 업로드된 영수증 이미지의 AI 분석 결과 및 최종 확정 데이터를 보관한다.
 */
@Entity
@Table(name = "receipt")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Receipt {

    /** 영수증 식별자 (PK) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "receipt_seq")
    private Long receiptSeq;

    /** 사용자 식별자 (FK) */
    @Column(name = "user_seq", nullable = false)
    private Long userSeq;

    /** 업로드된 이미지 파일 저장 경로 */
    @Column(name = "image_path", length = 512, nullable = false)
    private String imagePath;

    /** 최종 확정 금액 (ERP 입력용) */
    @Column(name = "pay_amount", nullable = false)
    private Integer payAmount;

    /** 최종 확정 사업자등록번호 (10자리) */
    @Column(name = "biz_no", length = 10, nullable = false)
    private String bizNo;

    /** 최종 확정 결제일 (YYYYMMDD 형식) */
    @Column(name = "pay_date", length = 8, nullable = false)
    private String payDate;

    /** 선택된 결제 카드사명 */
    @Column(name = "card_name", length = 100)
    private String cardName;

    /** 사업자번호 체크섬 검증 통과 여부 */
    @Column(name = "biz_no_valid", nullable = false)
    private Boolean bizNoValid;

    /** 카드 선택 방식 ('AUTO' | 'MANUAL') */
    @Column(name = "select_type", length = 10, nullable = false)
    private String selectType;

    /** AI가 추출해준 원본 JSON 데이터 */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "raw_json", columnDefinition = "jsonb")
    private String rawJson;

    /** 등록 일시 */
    @Builder.Default
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
