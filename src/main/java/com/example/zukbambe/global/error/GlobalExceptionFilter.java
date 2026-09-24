package com.example.zukbambe.global.error;

import com.example.zukbambe.global.error.exception.ErrorProperty;
import com.example.zukbambe.global.error.exception.GlobalErrorCode;
import com.example.zukbambe.global.error.exception.ZukBamException;
import com.example.zukbambe.global.error.response.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            switch (e) {
                case ZukBamException ze -> {
                    log.warn("{}: {} {}", ze.getClass().getSimpleName(), ze.getErrorCode().getStatus(), ze.getErrorCode().getMessage());
                    writeErrorResponse(response, ze.getErrorCode());
                }
                case Exception ex when ex.getCause() instanceof ZukBamException ze -> {
                    log.warn("{}: {} {}", ze.getClass().getSimpleName(), ze.getErrorCode().getStatus(), ze.getErrorCode().getMessage());
                    writeErrorResponse(response, ze.getErrorCode());
                }
                default -> {
                    log.error("Unhandled exception: {} {}", request.getMethod(), request.getRequestURI(), e);
                    writeErrorResponse(response, GlobalErrorCode.INTERNAL_ERROR);
                }
            }
        }
    }

    private void writeErrorResponse(HttpServletResponse response, ErrorProperty errorCode) throws IOException {
        response.resetBuffer();
        response.setStatus(errorCode.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8);
        objectMapper.writeValue(response.getWriter(), ErrorResponse.of(errorCode));
    }
}
