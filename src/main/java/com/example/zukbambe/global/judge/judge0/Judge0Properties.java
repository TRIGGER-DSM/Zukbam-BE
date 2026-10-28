package com.example.zukbambe.global.judge.judge0;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "judge0")
public record Judge0Properties(
    String url,
    Duration pollInterval,
    Duration requestTimeout,
    Duration idleTimeout
) {
}
