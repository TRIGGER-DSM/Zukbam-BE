package com.example.zukbambe.global.judge.judge0.dto;

import java.util.List;

public record Judge0BatchRequest(
    List<Judge0SubmissionRequest> submissions
) {
}
