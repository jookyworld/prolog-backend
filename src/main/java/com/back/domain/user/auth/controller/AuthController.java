package com.back.domain.user.auth.controller;

import com.back.domain.user.auth.dto.SignupRequest;
import com.back.domain.user.auth.service.AuthService;
import com.back.domain.user.user.dto.UserResponse;
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

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@Valid @RequestBody SignupRequest dto) {
        UserResponse userResponse = authService.signup(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @PostMapping("/login")
    public void login() {

    }

    @PostMapping("/logout")
    public void logout() {

    }

    @DeleteMapping("/deleteMe")
    public void deleteMe() {

    }
}
