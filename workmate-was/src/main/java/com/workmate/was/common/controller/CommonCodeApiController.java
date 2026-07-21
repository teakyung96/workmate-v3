package com.workmate.was.common.controller;

import com.workmate.was.common.service.CommonCodeService;
import com.workmate.was.common.vo.CommonCodeVo;
import com.workmate.was.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 공통코드 REST API (K1, F7-04). 예: 채팅 모델 드롭다운 구성용 AI_MODEL 조회.
 */
@Slf4j
@RestController
@RequestMapping("/api/common/codes")
@RequiredArgsConstructor
public class CommonCodeApiController {

    private final CommonCodeService commonCodeService;

    /** 그룹 코드 목록 조회 (존재하지 않는 그룹은 빈 배열, F7.3) */
    @GetMapping("/{groupCode}")
    public ApiResponse<List<CommonCodeVo>> getCodes(@PathVariable("groupCode") String groupCode) {
        return ApiResponse.success(commonCodeService.getCodes(groupCode));
    }
}
