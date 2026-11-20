package com.example.zukbambe.global.judge.judge0;

import com.example.zukbambe.domain.solution.domain.enums.Language;
import com.example.zukbambe.global.judge.enums.JudgeLanguage;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class Judge0LanguageTest {

    @Test
    void 모든_제출_언어는_채점_언어와_Judge0_언어로_이어진다() {
        Arrays.stream(Language.values()).forEach(language ->
            assertThat(Judge0Language.from(language.getJudgeLanguage()).getId()).isNotNull()
        );
    }

    @Test
    void 어셈블리는_NASM_id로_채점한다() {
        assertThat(Language.ASSEMBLY.getJudgeLanguage()).isEqualTo(JudgeLanguage.ASSEMBLY);
        assertThat(Judge0Language.from(JudgeLanguage.ASSEMBLY).getId()).isEqualTo(45);
    }
}
