package com.example.zukbambe.global.swagger.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Swagger 문서 전용 계정(swagger.auth)과 비교한다. 서비스 유저(tbl_users)와는 관계가 없다.
 */
@RequiredArgsConstructor
public class SwaggerAuthenticationProvider implements AuthenticationProvider {

    private final SwaggerAuthProperties properties;

    @Override
    public Authentication authenticate(Authentication authentication) {
        String username = authentication.getName();
        String password = String.valueOf(authentication.getCredentials());

        // 한쪽이 틀려도 둘 다 비교해서, 응답 시간으로 어느 쪽이 맞았는지 드러나지 않게 한다
        boolean usernameMatches = matches(username, properties.username());
        boolean passwordMatches = matches(password, properties.password());
        if (!usernameMatches || !passwordMatches) {
            throw new BadCredentialsException("Swagger 계정이 올바르지 않습니다");
        }

        return UsernamePasswordAuthenticationToken.authenticated(username, null, AuthorityUtils.NO_AUTHORITIES);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private boolean matches(String actual, String expected) {
        return MessageDigest.isEqual(
            actual.getBytes(StandardCharsets.UTF_8),
            expected.getBytes(StandardCharsets.UTF_8)
        );
    }
}
