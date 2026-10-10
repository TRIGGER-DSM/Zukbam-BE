package com.example.zukbambe.domain.auth.presentation.dto.response;

public record TokenResponse(
    String accessToken,
    String refreshToken
) {
}
