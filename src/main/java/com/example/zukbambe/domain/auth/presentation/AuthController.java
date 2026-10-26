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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth")
@SecurityRequirements
@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController {
    private final SignInService signInService;
    private final SendEmailVerificationService sendEmailVerificationService;
    private final VerifyEmailService verifyEmailService;
    private final StudentSignUpService studentSignUpService;
    private final TeacherSignUpService teacherSignUpService;

    @Operation(summary = "로그인")
    @PostMapping("/sign-in")
    public TokenResponse signIn(@Valid @RequestBody SignInRequest signInRequest) {
        return signInService.execute(signInRequest);
    }

    @Operation(summary = "이메일 인증 코드 발송", description = "인증 코드는 3분간 유효하다.")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/email-verifications")
    public EmailVerificationResponse sendEmailVerification(@Valid @RequestBody SendEmailVerificationRequest request) {
        return sendEmailVerificationService.execute(request);
    }

    @Operation(summary = "이메일 인증 코드 확인", description = "회원가입에 사용할 이메일 인증 토큰을 발급한다.")
    @PostMapping("/email-verifications/verify")
    public EmailVerifiedTokenResponse verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        return verifyEmailService.execute(request);
    }

    @Operation(summary = "학생 회원가입")
    @PostMapping("/sign-up")
    public TokenResponse studentSignUp(@Valid @RequestBody StudentSignUpRequest request) {
        return studentSignUpService.execute(request);
    }

    @Operation(summary = "선생님 회원가입")
    @PostMapping("/sign-up/admin")
    public TokenResponse teacherSignUp(@Valid @RequestBody TeacherSignUpRequest request) {
        return teacherSignUpService.execute(request);
    }
}
