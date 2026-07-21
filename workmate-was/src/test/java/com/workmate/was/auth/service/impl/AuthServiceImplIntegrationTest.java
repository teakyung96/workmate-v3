package com.workmate.was.auth.service.impl;

import com.workmate.was.auth.dao.UserRepository;
import com.workmate.was.auth.service.AuthService;
import com.workmate.was.auth.vo.LoginRequestVo;
import com.workmate.was.auth.vo.User;
import com.workmate.was.global.exception.AuthenticationFailedException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 로그인 실패 카운트 영속화 통합 테스트 (F1-06).
 *
 * <p>Mockito 단위 테스트는 트랜잭션 경계가 없어 "login() 이 예외로 롤백돼도 실패 기록은 남는다"는
 * REQUIRES_NEW 동작을 구조적으로 검증할 수 없다. 이 테스트는 실제 스프링 관리 빈과 실 DB 로
 * 그 롤백-생존을 증명한다 — 잘못된 비밀번호로 login() 이 401 을 던져도 fresh read 로 다시 읽으면
 * login_fail_count 가 실제 커밋되어 있어야 한다.</p>
 */
// AI 자동설정이 api-key 부재 시 빈 생성 단계에서 실패하므로, 이 테스트와 무관한 AI 키는 더미로 주입해
// 환경변수(GEMINI_API_KEY) 유무와 무관하게 컨텍스트가 뜨도록 한다. (AES 키는 다른 통합테스트와 동일하게 환경변수 필요)
@SpringBootTest(properties = {
        "spring.ai.google.genai.api-key=dummy-key-for-test",
        "spring.ai.google.genai.embedding.api-key=dummy-key-for-test"
})
class AuthServiceImplIntegrationTest {

    private static final String TEST_EMAIL = "integ-lockout-test@example.com";

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Long testUserSeq;

    @AfterEach
    void tearDown() {
        // 테스트가 만든 사용자만 정리 (운영/타 데이터 미영향)
        if (testUserSeq != null) {
            userRepository.deleteById(testUserSeq);
        }
    }

    @Test
    @DisplayName("잘못된 비밀번호로 login() 이 예외로 롤백돼도 실패 카운트는 별도 트랜잭션으로 커밋되어 남는다")
    void 실패_카운트는_login_롤백에도_영속화된다() {
        // given: 실제 DB 에 사용자 저장 (fail count 0)
        User user = User.builder()
                .email(TEST_EMAIL)
                .password(passwordEncoder.encode("abcd123!"))
                .userName("김태경")
                .phone("01012345678")
                .build();
        testUserSeq = userRepository.save(user).getUserSeq();
        assertThat(userRepository.findById(testUserSeq).orElseThrow().getLoginFailCount()).isZero();

        // when: 잘못된 비밀번호로 로그인 시도 → 401 (login() 트랜잭션은 롤백된다)
        LoginRequestVo request = new LoginRequestVo();
        request.setEmail(TEST_EMAIL);
        request.setPassword("wrong-pw1!");
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(AuthenticationFailedException.class);

        // then: fresh read — 롤백에도 불구하고 실패 카운트가 실제 DB 에 커밋되어 있어야 한다 (F1-06)
        User reloaded = userRepository.findById(testUserSeq).orElseThrow();
        assertThat(reloaded.getLoginFailCount()).isEqualTo(1);
    }
}
