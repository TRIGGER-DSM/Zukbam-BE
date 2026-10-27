package com.example.zukbambe.domain.user.facade;

import com.example.zukbambe.domain.user.domain.User;
import com.example.zukbambe.domain.user.domain.repository.UserRepository;
import com.example.zukbambe.domain.user.exception.UserNotFoundException;
import com.example.zukbambe.global.security.auth.CustomUserDetails;
import com.example.zukbambe.global.security.jwt.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserFacade {

    private final UserRepository userRepository;

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.userId();
        }
        throw new UnauthorizedException();
    }

    public User getCurrentUser() {
        return getUserById(getCurrentUserId());
    }

    // 비로그인 사용자도 호출할 수 있는 API에서 사용한다
    public Optional<User> findCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return Optional.of(getUserById(userDetails.userId()));
        }
        return Optional.empty();
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(UserNotFoundException::new);
    }
}
