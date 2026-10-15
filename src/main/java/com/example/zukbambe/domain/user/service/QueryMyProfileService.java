package com.example.zukbambe.domain.user.service;

import com.example.zukbambe.domain.solvedhistory.domain.repository.SolvedHistoryRepository;
import com.example.zukbambe.domain.student.domain.repository.StudentRepository;
import com.example.zukbambe.domain.user.domain.User;
import com.example.zukbambe.domain.user.facade.UserFacade;
import com.example.zukbambe.domain.user.presentation.dto.response.StudentInfoResponse;
import com.example.zukbambe.domain.user.presentation.dto.response.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QueryMyProfileService {

    private final UserFacade userFacade;
    private final StudentRepository studentRepository;
    private final SolvedHistoryRepository solvedHistoryRepository;

    @Transactional(readOnly = true)
    public UserProfileResponse execute() {
        User user = userFacade.getCurrentUser();
        Long userId = user.getUserId();

        // 선생님은 학생 정보가 없으므로 null로 응답
        StudentInfoResponse student = studentRepository.findById(userId)
            .map(StudentInfoResponse::from)
            .orElse(null);

        return UserProfileResponse.of(
            user,
            solvedHistoryRepository.countByIdUserId(userId),
            student
        );
    }
}
