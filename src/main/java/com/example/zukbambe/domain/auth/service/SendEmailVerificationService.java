package com.example.zukbambe.domain.auth.service;

import com.example.zukbambe.domain.auth.domain.EmailVerification;
import com.example.zukbambe.domain.auth.domain.repository.EmailVerificationRepository;
import com.example.zukbambe.domain.auth.presentation.dto.request.SendEmailVerificationRequest;
import com.example.zukbambe.domain.auth.presentation.dto.response.EmailVerificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class SendEmailVerificationService {
    private static final long EXPIRES_IN = 180L;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final EmailVerificationRepository emailVerificationRepository;
    private final JavaMailSender mailSender;

    public EmailVerificationResponse execute(SendEmailVerificationRequest request) {
        String code = String.format("%06d", RANDOM.nextInt(1_000_000));

        EmailVerification verification = emailVerificationRepository.save(
            EmailVerification.builder()
                .verificationId(UUID.randomUUID().toString())
                .email(request.getEmail())
                .code(code)
                .ttl(EXPIRES_IN)
                .build()
        );

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(request.getEmail());
        message.setSubject("[Zukbam] 이메일 인증 코드");
        message.setText("인증 코드: " + code + "\n" + EXPIRES_IN / 60 + "분 안에 입력해주세요.");
        mailSender.send(message);

        return new EmailVerificationResponse(verification.verificationId(), EXPIRES_IN);
    }
}
