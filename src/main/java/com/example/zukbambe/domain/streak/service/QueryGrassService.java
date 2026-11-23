package com.example.zukbambe.domain.streak.service;

import com.example.zukbambe.domain.solvedhistory.domain.repository.SolvedHistoryRepository;
import com.example.zukbambe.domain.streak.exception.InvalidGrassPeriodException;
import com.example.zukbambe.domain.streak.presentation.dto.response.GrassResponse;
import com.example.zukbambe.domain.user.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QueryGrassService {

    private final UserFacade userFacade;
    private final SolvedHistoryRepository solvedHistoryRepository;
    private final Clock clock;

    @Transactional(readOnly = true)
    public GrassResponse execute(Long userId, LocalDate from, LocalDate to) {
        LocalDate end = to != null ? to : LocalDate.now(clock);
        LocalDate start = from != null ? from : end.minusYears(1).plusDays(1);

        if (start.isAfter(end) || start.isBefore(end.minusYears(1).plusDays(1))) {
            throw new InvalidGrassPeriodException();
        }

        // 없는 유저는 빈 잔디가 아니라 404로 응답한다
        userFacade.getUserById(userId);

        // solved_time은 서비스 시계(한국 시간)로 저장되므로 날짜를 그대로 쓴다
        Map<LocalDate, Long> solvedCounts = solvedHistoryRepository.findSolvedDatesByUserIdAndPeriod(
                userId,
                start.atStartOfDay(),
                end.plusDays(1).atStartOfDay()
            ).stream()
            .collect(Collectors.groupingBy(LocalDateTime::toLocalDate, TreeMap::new, Collectors.counting()));

        return GrassResponse.of(start, end, solvedCounts);
    }
}
