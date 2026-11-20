package com.example.zukbambe.global.config;

import com.example.zukbambe.global.swagger.auth.SwaggerAuthProperties;
import com.example.zukbambe.global.swagger.auth.SwaggerAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SwaggerSecurityConfig {

    private static final String REALM = "Zukbam API Docs";

    private final SwaggerAuthProperties swaggerAuthProperties;

    // Swagger 문서 경로만 맡는 체인. API용 체인(SecurityConfig)보다 먼저 매칭되고, JWT가 아니라 문서 전용 계정(Basic)으로 로그인한다
    @Bean
    @Order(1)
    public SecurityFilterChain swaggerFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs", "/v3/api-docs/**")
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .logout(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        if (swaggerAuthProperties.isEnabled()) {
            http
                .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
                .authenticationManager(new ProviderManager(new SwaggerAuthenticationProvider(swaggerAuthProperties)))
                .httpBasic(basic -> basic.realmName(REALM));
        } else {
            http
                .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
                .httpBasic(AbstractHttpConfigurer::disable);
        }

        return http.build();
    }
}
