package com.example.zukbambe.global.security.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth.teacher")
public record TeacherProperties(
    String code
) {
}
