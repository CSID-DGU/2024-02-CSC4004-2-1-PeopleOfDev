package com.api.momentup.service;

import com.api.momentup.domain.*;
import com.api.momentup.exception.UserNotFoundException;
import com.api.momentup.repository.ChallengeJpaRepository;
import com.api.momentup.repository.UserJpaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.api.momentup.utils.AlarmUtils.isNotificationWithinChallengePeriod;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CalendarService {
    private final ChallengeJpaRepository challengeJpaRepository;
    private final UserJpaRepository userJpaRepository;
    @PersistenceContext
    private EntityManager entityManager;
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
                System.out.println("Is challenge managed: " + entityManager.contains(challenge));
                boolean isWithinRange = isNotificationWithinChallengePeriod(notification, challenge);
                if (isWithinRange) {
                    ChallengeDetail challengeDetail = ChallengeDetail.createChallengeDetail(CalendarResultType.FAIL);
                    challenge.addChallengeDetail(challengeDetail);
                }
            }
            System.out.println("Delayed task executed successfully for notification ID: " + notification.getNotificationNumber());
            entityManager.flush();
        } catch (Exception e) {
            System.err.println("Error executing delayed task: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void getCalendar(int month) {

    }
}
