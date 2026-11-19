package com.example.zukbambe.global.judge;

import com.example.zukbambe.global.judge.dto.JudgeRequest;
import com.example.zukbambe.global.judge.dto.JudgeResult;

public interface JudgeProvider {

    JudgeResult judge(JudgeRequest request);
}
