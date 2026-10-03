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

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(UserNotFoundException::new);
    }
}
