package com.example.zukbambe.domain.solution.domain.repository;

import com.example.zukbambe.domain.solution.domain.enums.Language;
import com.example.zukbambe.domain.solution.domain.enums.Status;

public interface SubmissionRow {
    Language getLanguage();

    Status getStatus();
}
