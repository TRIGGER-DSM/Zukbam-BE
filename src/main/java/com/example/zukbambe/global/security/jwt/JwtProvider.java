package com.example.zukbambe.global.security.jwt;

import com.example.zukbambe.domain.auth.domain.RefreshToken;
import com.example.zukbambe.domain.auth.domain.repository.RefreshTokenRepository;
import com.example.zukbambe.domain.user.domain.enums.Role;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtProperties jwtProperties;
    private final RefreshTokenRepository refreshTokenRepository;

    public String generateAccessToken(Long userId, Role role) {
        return generateToken(userId, role, TokenType.ACCESS);
    }

    public String generateRefreshToken(Long userId, Role role) {
        String token = generateToken(userId, role, TokenType.REFRESH);

        refreshTokenRepository.save(
            RefreshToken.builder()
                .userId(userId)
                .token(token)
                .ttl(jwtProperties.refreshExp())
                .build()
        );
        return token;
    }

    private String generateToken(Long userId, Role role, TokenType type) {
        Date now = new Date();
        return Jwts.builder()
            .id(UUID.randomUUID().toString())
            .subject(userId.toString())
            .claim("type", type.name())
            .claim("role", role.name())
            .issuedAt(now)
            .expiration(new Date(now.getTime() + jwtProperties.getExp(type) * 1000))
            .signWith(jwtProperties.secretKey())
            .compact();
    }
}
