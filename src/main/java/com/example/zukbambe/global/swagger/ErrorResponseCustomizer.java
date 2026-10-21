package com.example.zukbambe.global.swagger;

import com.example.zukbambe.global.error.exception.ErrorProperty;
import com.example.zukbambe.global.error.exception.GlobalErrorCode;
import com.example.zukbambe.global.error.response.ErrorResponse;
import com.example.zukbambe.global.security.jwt.exception.SecurityErrorCode;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.customizers.GlobalOperationCustomizer;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Component
@RequiredArgsConstructor
public class ErrorResponseCustomizer implements GlobalOperationCustomizer {
    private static final String JSON = "application/json";
    private static final String EXAMPLE_TIMESTAMP = "2026-01-01T12:00:00";

    private static final List<ErrorProperty> AUTH_ERROR_CODES = List.of(
        SecurityErrorCode.UNAUTHORIZED,
        SecurityErrorCode.INVALID_TOKEN,
        SecurityErrorCode.EXPIRED_TOKEN
    );

    private static final List<ErrorProperty> COMMON_ERROR_CODES = List.of(
        GlobalErrorCode.METHOD_NOT_ALLOWED,
        GlobalErrorCode.INTERNAL_ERROR
    );

    private final ThrownErrorCodeResolver thrownErrorCodeResolver;

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        Set<ErrorProperty> errorCodes = new LinkedHashSet<>();
        errorCodes.addAll(thrownErrorCodeResolver.resolve(handlerMethod.getMethod()));
        if (requiresAuth(handlerMethod)) {
            errorCodes.addAll(AUTH_ERROR_CODES);
        }
        errorCodes.addAll(COMMON_ERROR_CODES);

        Map<Integer, List<ErrorProperty>> byStatus = new TreeMap<>();
        for (ErrorProperty code : errorCodes) {
            byStatus.computeIfAbsent(code.getStatus().value(), k -> new ArrayList<>()).add(code);
        }

        ApiResponses responses = operation.getResponses();
        if (responses == null) {
            responses = new ApiResponses();
            operation.setResponses(responses);
        }

        for (Map.Entry<Integer, List<ErrorProperty>> entry : byStatus.entrySet()) {
            String status = String.valueOf(entry.getKey());
            if (responses.containsKey(status)) {
                continue;
            }
            responses.put(status, buildErrorResponse(entry.getKey(), entry.getValue()));
        }

        // 상태코드 순으로 정렬
        ApiResponses sorted = new ApiResponses();
        responses.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(e -> sorted.put(e.getKey(), e.getValue()));
        operation.setResponses(sorted);

        return operation;
    }

    private boolean requiresAuth(HandlerMethod handlerMethod) {
        SecurityRequirements requirements = handlerMethod.getMethodAnnotation(SecurityRequirements.class);
        if (requirements == null) {
            requirements = AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getBeanType(), SecurityRequirements.class);
        }
        return requirements == null || requirements.value().length > 0;
    }

    private ApiResponse buildErrorResponse(int status, List<ErrorProperty> codes) {
        Map<String, Example> examples = new LinkedHashMap<>();
        for (ErrorProperty code : codes) {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("timestamp", EXAMPLE_TIMESTAMP);
            body.put("status", code.getStatus().value());
            body.put("code", code.getCode());
            body.put("message", code.getMessage());
            examples.put(code.getCode(), new Example().summary(code.getMessage()).value(body));
        }

        MediaType mediaType = new MediaType()
            .schema(errorResponseSchema())
            .examples(examples);

        HttpStatus httpStatus = HttpStatus.resolve(status);
        String description = httpStatus != null ? httpStatus.getReasonPhrase() : String.valueOf(status);

        return new ApiResponse()
            .description(description)
            .content(new Content().addMediaType(JSON, mediaType));
    }

    private Schema<?> errorResponseSchema() {
        ResolvedSchema resolved = ModelConverters.getInstance()
            .resolveAsResolvedSchema(new AnnotatedType(ErrorResponse.class).resolveAsRef(false));
        return resolved.schema;
    }
}
