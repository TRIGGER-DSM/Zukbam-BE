package com.example.zukbambe.global.security.auth;

import com.example.zukbambe.domain.user.domain.repository.UserRepository;
import com.example.zukbambe.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(@NonNull String userId) {
        return userRepository.findById(Long.valueOf(userId))
            .map(user -> new CustomUserDetails(user.getUserId(), user.getRole()))
            .orElseThrow(UserNotFoundException::new);
    }
}
