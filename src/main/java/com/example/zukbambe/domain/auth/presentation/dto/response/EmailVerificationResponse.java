package com.example.zukbambe.domain.auth.presentation.dto.response;

public record EmailVerificationResponse(
    String verificationId,
    Long expiresIn
) {
}
