package kr.sparta.backendbasic2.controller;

import io.jsonwebtoken.Claims;
import kr.sparta.backendbasic2.entity.User;
import kr.sparta.backendbasic2.dto.AuthRequest;
import kr.sparta.backendbasic2.dto.AuthResponse;
import kr.sparta.backendbasic2.dto.RefreshRequest;
import kr.sparta.backendbasic2.repository.UserRepository;
import kr.sparta.backendbasic2.serivce.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepo;
    private final JwtTokenProvider jwt;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        User user = userRepo.findByUserId(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String accessToken = jwt.createAccessToken(createAuthentication(user.getUserId()));
        String refreshToken = jwt.createRefreshToken(user.getUserId());

        return new AuthResponse(accessToken, refreshToken);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestBody RefreshRequest request) {
        Claims claims = jwt.parse(request.getRefreshToken());
        String userId = claims.getSubject();
        User user = userRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String accessToken = jwt.createAccessToken(createAuthentication(userId));
        String refreshToken = jwt.createRefreshToken(user.getUserId());

        return new AuthResponse(accessToken, refreshToken);
    }

    private Authentication createAuthentication(String userId) {
        return new UsernamePasswordAuthenticationToken(
                userId,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}