package com.example.zukbambe.global.swagger.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

@ConfigurationProperties(prefix = "swagger.auth")
public record SwaggerAuthProperties(
    String username,
    String password
) {
    // 계정이 없으면 로그인 없이 연다
    public boolean isEnabled() {
        return StringUtils.hasText(username) && StringUtils.hasText(password);
    }
}
