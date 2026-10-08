package com.example.zukbambe.domain.auth.service;

import com.example.zukbambe.domain.auth.domain.EmailVerifiedToken;
import com.example.zukbambe.domain.auth.domain.RefreshToken;
import com.example.zukbambe.domain.auth.domain.repository.EmailVerifiedTokenRepository;
import com.example.zukbambe.domain.auth.domain.repository.RefreshTokenRepository;
import com.example.zukbambe.domain.auth.exception.EmailNotVerifiedException;
import com.example.zukbambe.domain.auth.presentation.dto.request.StudentSignUpRequest;
import com.example.zukbambe.domain.auth.presentation.dto.response.TokenResponse;
import com.example.zukbambe.domain.rank.domain.UserRank;
import com.example.zukbambe.domain.rank.domain.repository.UserRankRepository;
import com.example.zukbambe.domain.rank.exception.UserRankNotFoundException;
import com.example.zukbambe.domain.student.domain.Student;
import com.example.zukbambe.domain.student.domain.repository.StudentRepository;
import com.example.zukbambe.domain.user.domain.User;
import com.example.zukbambe.domain.user.domain.enums.Role;
import com.example.zukbambe.domain.user.domain.repository.UserRepository;
import com.example.zukbambe.domain.user.exception.UserAlreadyExistsException;
import com.example.zukbambe.global.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class StudentSignUpService {
    private static final String DEFAULT_RANK_NAME = "Unranked";

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final UserRankRepository userRankRepository;
    private final EmailVerifiedTokenRepository emailVerifiedTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;

    @Transactional
    public TokenResponse execute(StudentSignUpRequest request) {
        EmailVerifiedToken verifiedToken = emailVerifiedTokenRepository.findById(request.getEmailVerifiedToken())
            .filter(token -> token.email().equals(request.getEmail()))
            .orElseThrow(EmailNotVerifiedException::new);

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException();
        }

        UserRank rank = userRankRepository.findByName(DEFAULT_RANK_NAME)
            .orElseThrow(UserRankNotFoundException::new);

        User user = userRepository.save(
            User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .role(Role.STUDENT)
                .rank(rank)
                .build()
        );

        studentRepository.save(
            Student.builder()
                .user(user)
                .grade(request.getGrade())
                .classNumber(request.getClassNumber())
                .studentNumber(request.getNumber())
                .build()
        );

        emailVerifiedTokenRepository.delete(verifiedToken);

        String accessToken = jwtProvider.generateAccessToken(user.getUserId(), user.getRole());
        String refreshToken = jwtProvider.generateRefreshToken(user.getUserId(), user.getRole());

        refreshTokenRepository.save(
            RefreshToken.builder()
                .userId(user.getUserId())
                .token(refreshToken)
                .ttl(jwtProvider.getRefreshExp())
                .build()
        );

        return new TokenResponse(accessToken, refreshToken);
    }
}
