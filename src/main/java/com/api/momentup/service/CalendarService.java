package com.api.momentup.service;

import com.api.momentup.domain.*;
import com.api.momentup.dto.user.response.MonthCalendarDto;
import com.api.momentup.exception.UserNotFoundException;
import com.api.momentup.repository.CalendarJpaRepository;
import com.api.momentup.repository.ChallengeJpaRepository;
import com.api.momentup.repository.UserJpaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.api.momentup.utils.AlarmUtils.isNotificationWithinChallengePeriod;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CalendarService {
    private final ChallengeJpaRepository challengeJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final CalendarJpaRepository calendarJpaRepository;
    // 5분 지나면 실패 처리
    @Transactional
    public void inputFailResult(Long userNumber, Alarm alarm, UserNotification notification) {
        try {
            CalendarResultType resultType = CalendarResultType.FAIL;
            Calendar calendar = Calendar.createCalendar(resultType);
            Users findUser = userJpaRepository.findById(userNumber)
                    .orElseThrow(UserNotFoundException::new);
            findUser.addCalendar(calendar);

            List<Challenge> findChallenges = challengeJpaRepository.findByUsersAndGroups(findUser, alarm.getGroup());

            System.out.println("findChallenges size : "+findChallenges.size());

            for (Challenge challenge : findChallenges) {
                boolean isWithinRange = isNotificationWithinChallengePeriod(notification, challenge);
                if (isWithinRange) {
                    ChallengeDetail challengeDetail = ChallengeDetail.createChallengeDetail(CalendarResultType.FAIL);
                    challenge.addChallengeDetail(challengeDetail);
                }
            }
            System.out.println("Delayed task executed successfully for notification ID: " + notification.getNotificationNumber());
        } catch (Exception e) {
            System.err.println("Error executing delayed task: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<MonthCalendarDto> getCalendarsByUserAndMonth(Long userNumber, int year, int month) throws UserNotFoundException {
        Users findUser = userJpaRepository.findById(userNumber)
                .orElseThrow(UserNotFoundException::new);

        List<Calendar> calendars = calendarJpaRepository.findByUserAndYearAndMonth(userNumber, year, month);

        // Map을 사용해 중복 날짜 제거 및 result 계산
        Map<LocalDate, MonthCalendarDto> resultMap = new HashMap<>();

        for (Calendar calendar : calendars) {
            LocalDate date = calendar.getDate();
            CalendarResultType currentResult = calendar.getResult();

            resultMap.compute(date, (key, existingDto) -> {
                if (existingDto == null) {
                    // 최초 등록
                    return new MonthCalendarDto(
                            calendar.getCalendarNumber(),
                            date.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")),
                            currentResult
                    );
                } else {
                    // 기존 값 업데이트 (FAIL 우선)
                    return new MonthCalendarDto(
                            existingDto.getCalendarNumber(), // 첫 번째 데이터의 calendarNumber 사용
                            existingDto.getDate(),
                            existingDto.getResult() == CalendarResultType.FAIL || currentResult == CalendarResultType.FAIL
                                    ? CalendarResultType.FAIL
                                    : CalendarResultType.SUCCESS
                    );
                }
            });
        }

        return resultMap.values().stream()
                .sorted(Comparator.comparing(MonthCalendarDto::getDate))
                .collect(Collectors.toList());
    }
}
