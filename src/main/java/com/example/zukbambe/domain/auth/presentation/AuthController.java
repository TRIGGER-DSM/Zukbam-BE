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

    /**
     * 로그인<p>
     * 이메일과 비밀번호로 로그인해 액세스 토큰과 리프레시 토큰을 발급한다.
     * 이메일이 없는 경우와 비밀번호가 틀린 경우를 구분하지 않고 같은 에러로 응답한다.
     */
    @PostMapping("/sign-in")
    public TokenResponse signIn(@Valid @RequestBody SignInRequest signInRequest) {
        return signInService.execute(signInRequest);
    }

    /**
     * 이메일 인증 코드 발송<p>
     * dsm.hs.kr 이메일로 6자리 인증 코드를 보낸다. 인증 코드는 3분간 유효하다.
     * 응답의 verificationId는 인증 코드 확인에 사용한다.
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/email-verifications")
    public EmailVerificationResponse sendEmailVerification(@Valid @RequestBody SendEmailVerificationRequest request) {
        return sendEmailVerificationService.execute(request);
    }

    /**
     * 이메일 인증 코드 확인<p>
     * 인증 코드가 맞으면 회원가입에 사용할 이메일 인증 토큰을 발급한다.
     * 확인에 성공한 인증 코드는 다시 사용할 수 없다.
     */
    @PostMapping("/email-verifications/verify")
    public EmailVerifiedTokenResponse verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        return verifyEmailService.execute(request);
    }

    /**
     * 학생 회원가입<p>
     * 이메일 인증 토큰이 필요하며, 인증한 이메일과 가입하는 이메일이 같아야 한다.
     * dsm.hs.kr 이메일만 가입할 수 있다. 가입하면 바로 로그인된 상태의 토큰을 발급한다.
     */
    @PostMapping("/sign-up")
    public TokenResponse studentSignUp(@Valid @RequestBody StudentSignUpRequest request) {
        return studentSignUpService.execute(request);
    }

    /**
     * 선생님 회원가입<p>
     * 선생님 인증 코드와 이메일 인증 토큰이 필요하며, 인증한 이메일과 가입하는 이메일이 같아야 한다.
     * 가입하면 바로 로그인된 상태의 토큰을 발급한다.
     */
    @PostMapping("/sign-up/admin")
    public TokenResponse teacherSignUp(@Valid @RequestBody TeacherSignUpRequest request) {
        return teacherSignUpService.execute(request);
    }
}
