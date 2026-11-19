package com.example.zukbambe.global.judge.judge0.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Judge0SubmissionRequest(
    @JsonProperty("source_code") String sourceCode,
    @JsonProperty("language_id") Integer languageId,
    @JsonProperty("compiler_options") String compilerOptions,
    String stdin,
    @JsonProperty("expected_output") String expectedOutput,
    @JsonProperty("cpu_time_limit") Double cpuTimeLimit,
    @JsonProperty("memory_limit") Long memoryLimit
) {
}
