package com.example.zukbambe.domain.auth.service;

import com.example.zukbambe.domain.auth.domain.EmailVerification;
import com.example.zukbambe.domain.auth.domain.EmailVerifiedToken;
import com.example.zukbambe.domain.auth.domain.repository.EmailVerificationRepository;
import com.example.zukbambe.domain.auth.domain.repository.EmailVerifiedTokenRepository;
import com.example.zukbambe.domain.auth.exception.VerificationCodeMismatchException;
import com.example.zukbambe.domain.auth.exception.VerificationNotFoundException;
import com.example.zukbambe.domain.auth.presentation.dto.request.VerifyEmailRequest;
import com.example.zukbambe.domain.auth.presentation.dto.response.EmailVerifiedTokenResponse;
import com.example.zukbambe.global.mail.EmailProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class VerifyEmailService {
    private final EmailVerificationRepository emailVerificationRepository;
    private final EmailVerifiedTokenRepository emailVerifiedTokenRepository;
    private final EmailProperties emailProperties;

    public EmailVerifiedTokenResponse execute(VerifyEmailRequest request) {
        EmailVerification verification = emailVerificationRepository.findById(request.getVerificationId())
            .filter(v -> v.email().equals(request.getEmail()))
            .orElseThrow(VerificationNotFoundException::new);

        if (!verification.code().equals(request.getVerificationCode())) {
            throw new VerificationCodeMismatchException();
        }

        emailVerificationRepository.delete(verification);

        EmailVerifiedToken token = emailVerifiedTokenRepository.save(
            EmailVerifiedToken.builder()
                .token(UUID.randomUUID().toString())
                .email(request.getEmail())
                .ttl(emailProperties.verifiedTokenExp())
                .build()
        );

        return new EmailVerifiedTokenResponse(token.token());
    }
}
