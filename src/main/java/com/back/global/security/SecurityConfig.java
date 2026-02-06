package com.back.global.security;

import com.back.domain.user.user.repository.UserRepository;
import com.back.global.security.jwt.JwtAuthenticationFilter;
import com.back.global.security.jwt.JwtTokenProvider;
import com.back.global.security.jwt.JwtTokenResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    // 허용 URL 패턴
    private static final String[] ALWAYS_PERMIT = {
            "/api/auth/signup",
            "/api/auth/login",
            "/api/auth/refresh"
    };

    private static final String[] ALWAYS_AUTHENTICATED = {
            "/api/auth/me",
    };

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final JwtTokenResolver jwtTokenResolver;

    private JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider, jwtTokenResolver, userRepository);
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(ALWAYS_PERMIT).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
