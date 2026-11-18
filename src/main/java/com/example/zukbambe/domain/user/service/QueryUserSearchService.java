package com.example.zukbambe.domain.user.service;

import com.example.zukbambe.domain.student.domain.Student;
import com.example.zukbambe.domain.student.domain.repository.StudentRepository;
import com.example.zukbambe.domain.user.domain.User;
import com.example.zukbambe.domain.user.domain.repository.UserRepository;
import com.example.zukbambe.domain.user.facade.UserFacade;
import com.example.zukbambe.domain.user.presentation.dto.response.StudentInfoResponse;
import com.example.zukbambe.domain.user.presentation.dto.response.UserSearchListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QueryUserSearchService {

    private static final Pattern STUDENT_CODE = Pattern.compile("\\d{4}");

    private final UserFacade userFacade;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    @Transactional(readOnly = true)
    public UserSearchListResponse execute(String keyword, int page, int size) {
        Long myUserId = userFacade.getCurrentUserId();
        String trimmed = keyword.trim();
        PageRequest pageRequest = PageRequest.of(page, size);

        // 숫자 4자리면 학번으로, 아니면 이름으로 찾는다
        Page<User> users = STUDENT_CODE.matcher(trimmed).matches()
            ? userRepository.searchByStudentCode(myUserId, Integer.parseInt(trimmed), pageRequest)
            : userRepository.searchByName(myUserId, trimmed, pageRequest);

        List<Long> userIds = users.getContent().stream().map(User::getUserId).toList();
        Map<Long, StudentInfoResponse> studentByUserId = studentRepository.findAllById(userIds).stream()
            .collect(Collectors.toMap(Student::getUserId, StudentInfoResponse::from));

        return UserSearchListResponse.of(users, studentByUserId);
    }
}
