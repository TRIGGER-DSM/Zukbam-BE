package com.example.zukbambe.global.config;

import com.example.zukbambe.domain.user.domain.enums.Role;
import com.example.zukbambe.global.error.exception.ZukBamException;
import com.example.zukbambe.global.security.jwt.JwtFilter;
import com.example.zukbambe.global.security.jwt.JwtParser;
import com.example.zukbambe.global.security.jwt.exception.ForbiddenException;
import com.example.zukbambe.global.security.jwt.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtParser jwtParser;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .logout(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/error").permitAll()
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/problems").hasRole(Role.TEACHER.name())
                .requestMatchers(HttpMethod.PUT, "/problems/*").hasRole(Role.TEACHER.name())
                .requestMatchers(HttpMethod.DELETE, "/problems/*").hasRole(Role.TEACHER.name())
                .anyRequest().authenticated()
            )

            .exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, e) -> {
                    if (request.getAttribute(JwtFilter.EXCEPTION_ATTRIBUTE) instanceof ZukBamException tokenException) {
                        throw tokenException;
                    }
                    throw new UnauthorizedException();
                })
                .accessDeniedHandler((request, response, e) -> {
                    throw new ForbiddenException();
                })
            )

            .addFilterBefore(new JwtFilter(jwtParser), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
