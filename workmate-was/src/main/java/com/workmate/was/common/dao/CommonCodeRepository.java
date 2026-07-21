package com.workmate.was.common.dao;

import com.workmate.was.common.vo.CommonCode;
import com.workmate.was.common.vo.CommonCodeId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 공통코드 조회 Repository (F7).
 */
public interface CommonCodeRepository extends JpaRepository<CommonCode, CommonCodeId> {

    /** 그룹의 사용 코드 목록 (정렬순, K1) */
    List<CommonCode> findByGroupCodeAndUseYnTrueOrderBySortOrder(String groupCode);

    /** 화이트리스트 검증용 — 해당 그룹에 사용 중 코드가 존재하는지 (F9-04) */
    boolean existsByGroupCodeAndCodeAndUseYnTrue(String groupCode, String code);
}
