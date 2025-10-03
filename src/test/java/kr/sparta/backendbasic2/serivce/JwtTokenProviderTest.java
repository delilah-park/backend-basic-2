package kr.sparta.backendbasic2.serivce;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        authentication = new UsernamePasswordAuthenticationToken(
                "testUser",
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Test
    @DisplayName("액세스 토큰 생성 성공")
    void createAccessToken_Success() {
        // when
        String token = jwtTokenProvider.createAccessToken(authentication);

        // then
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    @DisplayName("리프레시 토큰 생성 성공")
    void createRefreshToken_Success() {
        // given
        String userId = "testUser";

        // when
        String token = jwtTokenProvider.createRefreshToken(userId);

        // then
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    @DisplayName("유효한 토큰 파싱 성공")
    void parseToken_ValidToken_Success() {
        // given
        String token = jwtTokenProvider.createAccessToken(authentication);

        // when
        Claims claims = jwtTokenProvider.parse(token);

        // then
        assertNotNull(claims);
        assertEquals("testUser", claims.getSubject());
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
        assertTrue(claims.getExpiration().after(new Date()));
    }

    @Test
    @DisplayName("리프레시 토큰 파싱 성공")
    void parseRefreshToken_Success() {
        // given
        String userId = "testUser";
        String refreshToken = jwtTokenProvider.createRefreshToken(userId);

        // when
        Claims claims = jwtTokenProvider.parse(refreshToken);

        // then
        assertNotNull(claims);
        assertEquals(userId, claims.getSubject());
        assertNotNull(claims.getExpiration());
        assertTrue(claims.getExpiration().after(new Date()));
    }

    @Test
    @DisplayName("잘못된 토큰 파싱 실패")
    void parseToken_InvalidToken_ThrowsException() {
        // given
        String invalidToken = "invalid.token.here";

        // when & then
        assertThrows(MalformedJwtException.class, () -> {
            jwtTokenProvider.parse(invalidToken);
        });
    }

    @Test
    @DisplayName("빈 토큰 파싱 실패")
    void parseToken_EmptyToken_ThrowsException() {
        // given
        String emptyToken = "";

        // when & then
        assertThrows(Exception.class, () -> {
            jwtTokenProvider.parse(emptyToken);
        });
    }

    @Test
    @DisplayName("액세스 토큰에 사용자 정보와 권한 포함 확인")
    void accessToken_ContainsUserInfoAndRoles() {
        // given
        String token = jwtTokenProvider.createAccessToken(authentication);

        // when
        Claims claims = jwtTokenProvider.parse(token);

        // then
        assertEquals("testUser", claims.getSubject());
        assertNotNull(claims.get("roles"));
    }

    @Test
    @DisplayName("토큰 만료 시간 검증 - 액세스 토큰")
    void accessToken_ExpirationTime() {
        // given
        long beforeCreation = System.currentTimeMillis();
        String token = jwtTokenProvider.createAccessToken(authentication);
        long afterCreation = System.currentTimeMillis();

        // when
        Claims claims = jwtTokenProvider.parse(token);

        // then
        Date expiration = claims.getExpiration();
        Date issuedAt = claims.getIssuedAt();

        // 60분  유효기간 확인 - 약간의 오차 허용
        long actualDuration = expiration.getTime() - issuedAt.getTime();
        long expectedDuration = 60 * 60 * 1000;
        assertEquals(expectedDuration, actualDuration);

        // 발급 시간이 적절한 범위 내에 있는지 확인 (1초 오차 허용)
        assertTrue(issuedAt.getTime() >= beforeCreation - 1000);
        assertTrue(issuedAt.getTime() <= afterCreation + 1000);

        // 만료 시간이 미래인지 확인
        assertTrue(expiration.after(new Date()));
    }

    @Test
    @DisplayName("토큰 만료 시간 검증 - 리프레시 토큰")
    void refreshToken_ExpirationTime() {
        // given
        String userId = "testUser";
        long beforeCreation = System.currentTimeMillis();
        String token = jwtTokenProvider.createRefreshToken(userId);

        // when
        Claims claims = jwtTokenProvider.parse(token);

        // then
        Date expiration = claims.getExpiration();

        // 1일 후 만료 확인 (현재 시간 + 24시간)
        long expectedMinExpiration = beforeCreation + (24 * 60 * 60 * 1000) - 1000; // 1초 오차 허용
        assertTrue(expiration.getTime() >= expectedMinExpiration);
    }

}