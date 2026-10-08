package com.example.zukbambe.domain.auth.service;

import com.example.zukbambe.domain.auth.domain.RefreshToken;
import com.example.zukbambe.domain.auth.domain.repository.RefreshTokenRepository;
import com.example.zukbambe.domain.auth.exception.InvalidCredentialsException;
import com.example.zukbambe.domain.auth.presentation.dto.request.SignInRequest;
import com.example.zukbambe.domain.auth.presentation.dto.response.TokenResponse;
import com.example.zukbambe.domain.user.domain.User;
import com.example.zukbambe.domain.user.domain.repository.UserRepository;
import com.example.zukbambe.global.security.jwt.JwtProperties;
import com.example.zukbambe.global.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SignInService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final JwtProperties jwtProperties;
    private final PasswordEncoder passwordEncoder;

    public TokenResponse execute(SignInRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        String accessToken = jwtProvider.generateAccessToken(user.getUserId(), user.getRole());
        String refreshToken = jwtProvider.generateRefreshToken(user.getUserId(), user.getRole());

        refreshTokenRepository.save(
            RefreshToken.builder()
                .userId(user.getUserId())
                .token(refreshToken)
                .ttl(jwtProperties.refreshExp())
                .build()
        );

        return new TokenResponse(accessToken, refreshToken);
    }
}
