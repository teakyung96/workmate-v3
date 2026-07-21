package com.workmate.was.common.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 공통코드 Entity (common_code, 04 §4.5). 그룹코드+코드 복합 PK.
 */
@Entity
@Table(name = "common_code")
@IdClass(CommonCodeId.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommonCode {

    @Id
    @Column(name = "group_code", length = 30)
    private String groupCode;

    @Id
    @Column(name = "code", length = 50)
    private String code;

    @Column(name = "code_name", nullable = false, length = 100)
    private String codeName;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(name = "use_yn", nullable = false)
    private boolean useYn;
}
