package com.workmate.was.admin.service.impl;

import com.workmate.was.admin.dao.AdminAuditLogRepository;
import com.workmate.was.admin.vo.AdminAuditLog;
import com.workmate.was.admin.vo.ResetPasswordResultVo;
import com.workmate.was.admin.vo.UserPageVo;
import com.workmate.was.auth.dao.UserRepository;
import com.workmate.was.auth.util.PasswordPolicyValidator;
import com.workmate.was.auth.vo.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * AdminServiceImpl 단위 테스트 (목록 마스킹·잠금 해제·비밀번호 초기화·감사 기록).
 */
@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private AdminAuditLogRepository adminAuditLogRepository;

    private AdminServiceImpl adminService;

    @BeforeEach
    void setUp() {
        adminService = new AdminServiceImpl(userRepository, adminAuditLogRepository, new BCryptPasswordEncoder());
    }

    private User user(Long userSeq, String email, String name, String phone, LocalDateTime lockedAt) {
        User u = User.builder().email(email).password("hash").userName(name).phone(phone).role("ROLE_USER").build();
        ReflectionTestUtils.setField(u, "userSeq", userSeq);
        if (lockedAt != null) {
            ReflectionTestUtils.setField(u, "lockedAt", lockedAt);
            ReflectionTestUtils.setField(u, "loginFailCount", 5);
        }
        return u;
    }

    @Test
    @DisplayName("getUsers(이름검색): 이메일·전화 마스킹 + 페이징 메타 매핑")
    void getUsers_masks_and_maps() {
        User u = user(1L, "kim@gmail.com", "김태경", "01012345678", null);
        when(userRepository.findByUserNameContainingIgnoreCase(eq("김"), any()))
                .thenReturn(new PageImpl<>(List.of(u), PageRequest.of(0, 20), 1));

        UserPageVo result = adminService.getUsers("김", 0, 20);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getMaskedEmail()).isEqualTo("k**@g***.com");
        assertThat(result.getContent().get(0).getMaskedPhone()).isEqualTo("010****5678");
        assertThat(result.getContent().get(0).getUserName()).isEqualTo("김태경");
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("getUsers: 잠긴 계정은 locked=true 로 표시")
    void getUsers_marks_locked() {
        User locked = user(2L, "a@b.com", "박민수", "01099998888", LocalDateTime.now());
        when(userRepository.findByUserNameContainingIgnoreCase(any(), any()))
                .thenReturn(new PageImpl<>(List.of(locked)));

        UserPageVo result = adminService.getUsers("박", 0, 20);

        assertThat(result.getContent().get(0).isLocked()).isTrue();
    }

    @Test
    @DisplayName("unlock: 실패 상태를 초기화하고 감사 로그를 남긴다")
    void unlock_resets_and_audits() {
        User locked = user(5L, "a@b.com", "홍길동", "01011112222", LocalDateTime.now());
        when(userRepository.findById(5L)).thenReturn(Optional.of(locked));

        adminService.unlock(1L, 5L);

        assertThat(locked.getLockedAt()).isNull();
        assertThat(locked.getLoginFailCount()).isZero();
        verify(adminAuditLogRepository).save(any(AdminAuditLog.class));
    }

    @Test
    @DisplayName("resetPassword: 정책 충족 임시비번 발급·BCrypt 저장·감사 로그, 평문 반환")
    void resetPassword_issues_temp() {
        User u = user(5L, "a@b.com", "홍길동", "01011112222", null);
        when(userRepository.findById(5L)).thenReturn(Optional.of(u));

        ResetPasswordResultVo result = adminService.resetPassword(1L, 5L);

        assertThat(result.getTempPassword()).isNotBlank();
        // 정책(8+·영문·숫자·특수) 충족
        assertThatCode(() -> PasswordPolicyValidator.validate(result.getTempPassword()))
                .doesNotThrowAnyException();
        // 저장된 비번이 발급 평문의 BCrypt 해시
        assertThat(new BCryptPasswordEncoder().matches(result.getTempPassword(), u.getPassword())).isTrue();
        verify(adminAuditLogRepository).save(any(AdminAuditLog.class));
    }

    @Test
    @DisplayName("존재하지 않는 사용자에 대한 조치는 IllegalArgumentException")
    void action_on_missing_user_throws() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.unlock(1L, 99L))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
