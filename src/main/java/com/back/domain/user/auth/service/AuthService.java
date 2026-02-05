package com.back.domain.user.auth.service;

import com.back.domain.user.auth.dto.LoginRequest;
import com.back.domain.user.auth.dto.SignupRequest;
import com.back.domain.user.user.dto.UserResponse;
import com.back.domain.user.user.entity.Gender;
import com.back.domain.user.user.entity.Role;
import com.back.domain.user.user.entity.User;
import com.back.domain.user.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse signup(SignupRequest dto) {

        if (userRepository.existsByUsername(dto.username())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        if (userRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        if (userRepository.existsByNickname(dto.nickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        String encodedPassword = passwordEncoder.encode(dto.password());

        Gender gender = dto.gender() != null ? dto.gender() : Gender.UNKNOWN;

        User user = User.builder()
                .username(dto.username())
                .password(encodedPassword)
                .email(dto.email())
                .nickname(dto.nickname())
                .gender(gender)
                .height(dto.height())
                .weight(dto.weight())
                .role(Role.USER)
                .build();

        User saved = userRepository.save(user);

        return UserResponse.from(saved);
    }

    public User login(LoginRequest dto) {
        User user = userRepository.findByUsername(dto.username())
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다"));

        if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다");
        }

        return user;
    }

    public void logout() {

    }

    public void deleteMe() {

    }
}
