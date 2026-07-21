package com.workmate.web.global.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class WasAuthenticationProviderTest {

    private MockRestServiceServer server;
    private WasAuthenticationProvider provider;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://localhost:8081");
        server = MockRestServiceServer.bindTo(builder).build();
        provider = new WasAuthenticationProvider(builder.build());
    }

    private Authentication token() {
        return new UsernamePasswordAuthenticationToken("user@example.com", "abcd123!");
    }

    @Test
    void 인증_성공_시_LoginUser_principal_과_역할이_설정된다() {
        server.expect(requestTo("http://localhost:8081/api/v1/auth/login"))
                .andRespond(withSuccess(
                        "{\"success\":true,\"message\":\"success\",\"result\":{\"userSeq\":1,\"userName\":\"김태경\",\"role\":\"ROLE_USER\"}}",
                        MediaType.APPLICATION_JSON));

        Authentication result = provider.authenticate(token());

        LoginUser principal = (LoginUser) result.getPrincipal();
        assertThat(principal.getUserSeq()).isEqualTo(1L);
        assertThat(result.getAuthorities()).extracting("authority").containsExactly("ROLE_USER");
    }

    @Test
    void WAS_401_은_BadCredentialsException_으로_변환된다() {
        server.expect(requestTo("http://localhost:8081/api/v1/auth/login"))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"success\":false,\"message\":\"이메일 또는 비밀번호가 올바르지 않습니다.\",\"result\":null}"));

        assertThatThrownBy(() -> provider.authenticate(token()))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void WAS_409_잠금은_LockedException_으로_변환되고_메시지를_유지한다() {
        server.expect(requestTo("http://localhost:8081/api/v1/auth/login"))
                .andRespond(withStatus(HttpStatus.CONFLICT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"success\":false,\"message\":\"계정이 잠겼습니다. 42분 후 다시 시도해주세요.\",\"result\":null}"));

        assertThatThrownBy(() -> provider.authenticate(token()))
                .isInstanceOf(LockedException.class)
                .hasMessageContaining("42분");
    }
}
