package com.example.zukbambe.domain.user.presentation.dto.response;

import com.example.zukbambe.domain.student.domain.Student;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record StudentInfoResponse(
    Short grade,
    @JsonProperty("class") Short classNumber,
    Short number
) {
    public static StudentInfoResponse from(Student student) {
        return StudentInfoResponse.builder()
            .grade(student.getGrade())
            .classNumber(student.getClassNumber())
            .number(student.getStudentNumber())
            .build();
    }
}
