package kr.sparta.backendbasic2.serivce;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    // 고정된 비밀 키 문자열로 Key 객체 생성
    private final Key key = Keys.hmacShaKeyFor(
            "mySecretKey123456789012345678901234567890".getBytes(StandardCharsets.UTF_8)
    );

    private final long accessValidity = 1000 * 60 * 60;  // 60분
    private final long refreshValidity = 1000L * 60 * 60 * 24; // 1일

    public String createAccessToken(Authentication authentication) {
        String userId = authentication.getName();
        return Jwts.builder()
                .setSubject(userId)
                .claim("roles", authentication.getAuthorities())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessValidity))
                .signWith(key)
                .compact();
    }

public String createRefreshToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshValidity))
                .signWith(key)
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}