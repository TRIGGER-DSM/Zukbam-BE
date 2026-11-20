package com.example.zukbambe.global.judge;

import com.example.zukbambe.global.judge.dto.JudgeRequest;
import com.example.zukbambe.global.judge.dto.JudgeResult;
import com.example.zukbambe.global.judge.dto.JudgeRunResult;

import java.util.List;

public interface JudgeProvider {

    JudgeResult judge(JudgeRequest request);

    // 기대 출력 없이 cases의 input만 실행하고, cases 순서대로 결과를 돌려준다
    List<JudgeRunResult> run(JudgeRequest request);
}
