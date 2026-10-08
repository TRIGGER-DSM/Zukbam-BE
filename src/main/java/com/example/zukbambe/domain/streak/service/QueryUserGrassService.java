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
import java.time.ZoneId;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QueryUserGrassService {

    private final UserFacade userFacade;
    private final SolvedHistoryRepository solvedHistoryRepository;
    private final Clock clock;

    @Transactional(readOnly = true)
    public GrassResponse execute(Long userId, LocalDate from, LocalDate to) {
        LocalDate end = to != null ? to : LocalDate.now(clock);
        LocalDate start = from != null ? from : end.minusYears(1).plusDays(1);

        if (start.isAfter(end) || start.isBefore(end.minusYears(1))) {
            throw new InvalidGrassPeriodException();
        }

        // 없는 유저는 빈 잔디가 아니라 404로 응답한다
        userFacade.getUserById(userId);

        Map<LocalDate, Long> solvedCounts = solvedHistoryRepository.findSolvedDatesByUserIdAndPeriod(
                userId,
                toAuditTime(start),
                toAuditTime(end.plusDays(1))
            ).stream()
            .collect(Collectors.groupingBy(this::toServiceDate, TreeMap::new, Collectors.counting()));

        return GrassResponse.of(start, end, solvedCounts);
    }

    // solved_time은 Auditing이 서버 기본 시간대로 저장하므로, 서비스 시간대의 날짜와 서로 변환해 준다
    private LocalDateTime toAuditTime(LocalDate date) {
        return date.atStartOfDay(clock.getZone())
            .withZoneSameInstant(ZoneId.systemDefault())
            .toLocalDateTime();
    }

    private LocalDate toServiceDate(LocalDateTime solvedDate) {
        return solvedDate.atZone(ZoneId.systemDefault())
            .withZoneSameInstant(clock.getZone())
            .toLocalDate();
    }
}
