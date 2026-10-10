package com.example.zukbambe.domain.auth.presentation;

import com.example.zukbambe.domain.auth.presentation.dto.request.SendEmailVerificationRequest;
import com.example.zukbambe.domain.auth.presentation.dto.request.SignInRequest;
import com.example.zukbambe.domain.auth.presentation.dto.request.StudentSignUpRequest;
import com.example.zukbambe.domain.auth.presentation.dto.request.TeacherSignUpRequest;
import com.example.zukbambe.domain.auth.presentation.dto.request.VerifyEmailRequest;
import com.example.zukbambe.domain.auth.presentation.dto.response.EmailVerificationResponse;
import com.example.zukbambe.domain.auth.presentation.dto.response.EmailVerifiedTokenResponse;
import com.example.zukbambe.domain.auth.presentation.dto.response.TokenResponse;
import com.example.zukbambe.domain.auth.service.SendEmailVerificationService;
import com.example.zukbambe.domain.auth.service.SignInService;
import com.example.zukbambe.domain.auth.service.StudentSignUpService;
import com.example.zukbambe.domain.auth.service.TeacherSignUpService;
import com.example.zukbambe.domain.auth.service.VerifyEmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController {
    private final SignInService signInService;
    private final SendEmailVerificationService sendEmailVerificationService;
    private final VerifyEmailService verifyEmailService;
    private final StudentSignUpService studentSignUpService;
    private final TeacherSignUpService teacherSignUpService;

    @PostMapping("/sign-in")
    public TokenResponse signIn(@Valid @RequestBody SignInRequest signInRequest) {
        return signInService.execute(signInRequest);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/email-verifications")
    public EmailVerificationResponse sendEmailVerification(@Valid @RequestBody SendEmailVerificationRequest request) {
        return sendEmailVerificationService.execute(request);
    }

    @PostMapping("/email-verifications/verify")
    public EmailVerifiedTokenResponse verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        return verifyEmailService.execute(request);
    }

    @PostMapping("/sign-up")
    public TokenResponse studentSignUp(@Valid @RequestBody StudentSignUpRequest request) {
        return studentSignUpService.execute(request);
    }

    @PostMapping("/sign-up/admin")
    public TokenResponse teacherSignUp(@Valid @RequestBody TeacherSignUpRequest request) {
        return teacherSignUpService.execute(request);
    }
}
