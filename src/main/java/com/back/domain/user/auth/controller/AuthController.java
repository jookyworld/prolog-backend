package com.back.domain.user.auth.controller;

import com.back.domain.user.auth.dto.LoginRequest;
import com.back.domain.user.auth.dto.LoginResponse;
import com.back.domain.user.auth.dto.SignupRequest;
import com.back.domain.user.auth.service.AuthService;
import com.back.domain.user.user.dto.UserResponse;
import com.back.domain.user.user.entity.User;
import com.back.global.cookieManager.CookieManager;
import com.back.global.security.jwt.JwtTokenProvider;
import com.back.global.security.token.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final CookieManager cookieManager;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@Valid @RequestBody SignupRequest dto) {
        UserResponse userResponse = authService.signup(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest dto) {
        User user = authService.login(dto);

        // 토큰 발급
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getRole());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        // refreshToken 저장
        refreshTokenService.saveRefreshToken(user.getId(), refreshToken);

        // 쿠키에 토큰 넣기
        cookieManager.setAccessToken(accessToken);
        cookieManager.setRefreshToken(refreshToken);

        UserResponse userResponse = UserResponse.from(user);

        return ResponseEntity.ok(LoginResponse.from(userResponse, accessToken, refreshToken));
    }

    @PostMapping("/logout")
    public void logout() {

    }

    @DeleteMapping("/deleteMe")
    public void deleteMe() {

    }
}
