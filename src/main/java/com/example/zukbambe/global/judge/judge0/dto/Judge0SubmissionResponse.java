package com.example.zukbambe.global.judge.judge0.dto;

// time은 "0.001" 같은 초 단위 문자열, memory는 KB
public record Judge0SubmissionResponse(
    String token,
    Judge0StatusResponse status,
    String time,
    Long memory
) {
}
