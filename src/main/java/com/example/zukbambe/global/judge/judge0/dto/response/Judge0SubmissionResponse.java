package com.example.zukbambe.global.judge.judge0.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

// time은 "0.001" 같은 초 단위 문자열, memory는 KB. stdout, stderr, compileOutput은 요청할 때만 오고 base64로 인코딩됨
public record Judge0SubmissionResponse(
    String token,
    Judge0StatusResponse status,
    String time,
    Long memory,
    String stdout,
    String stderr,
    @JsonProperty("compile_output") String compileOutput
) {
}
