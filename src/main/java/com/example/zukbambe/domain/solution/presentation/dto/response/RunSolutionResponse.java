package com.example.zukbambe.domain.solution.presentation.dto.response;

import com.example.zukbambe.domain.solution.domain.enums.Status;
import com.example.zukbambe.global.judge.dto.JudgeRunResult;

import java.util.List;

public record RunSolutionResponse(
    List<Result> results
) {
    public static RunSolutionResponse from(List<JudgeRunResult> results) {
        return new RunSolutionResponse(results.stream().map(Result::from).toList());
    }

    public record Result(
        Status status,
        String stdout,
        String stderr,
        String compileOutput,
        Long memoryUsage,
        Double time
    ) {
        static Result from(JudgeRunResult result) {
            return new Result(
                Status.from(result.status()),
                result.stdout(),
                result.stderr(),
                result.compileOutput(),
                result.memory(),
                result.time()
            );
        }
    }
}
