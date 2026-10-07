package com.example.zukbambe.domain.problem.presentation.dto.request;

import com.example.zukbambe.domain.problem.domain.enums.ProblemRank;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ProblemRequest(
    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 255, message = "제목은 255자 이하여야 합니다.")
    String title,

    @NotBlank(message = "내용은 필수입니다.")
    String content,

    @NotNull(message = "메모리 제한은 필수입니다.")
    @Positive(message = "메모리 제한은 양수여야 합니다.")
    Long memoryLimit,

    @NotNull(message = "시간 제한은 필수입니다.")
    @Positive(message = "시간 제한은 양수여야 합니다.")
    Long timeLimit,

    @NotNull(message = "난이도는 필수입니다.")
    ProblemRank rank,

    @NotEmpty(message = "테스트케이스는 1개 이상이어야 합니다.")
    @Valid
    List<@NotNull(message = "테스트케이스는 null일 수 없습니다.") TestCaseRequest> testCases
) {
}
