package com.example.zukbambe.global.mail;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth.email")
public record EmailProperties(
    Long verifiedTokenExp
) {
}
