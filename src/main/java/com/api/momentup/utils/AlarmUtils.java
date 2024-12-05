package com.api.momentup.utils;

import com.api.momentup.domain.Challenge;
import com.api.momentup.domain.UserNotification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.ThreadLocalRandom;

public class AlarmUtils {
    public static LocalDateTime generateRandomTime(LocalTime startTime, LocalTime endTime) {
        long startSeconds = startTime.toSecondOfDay();
        long endSeconds = endTime.toSecondOfDay();
        long randomSeconds = ThreadLocalRandom.current().nextLong(startSeconds, endSeconds + 1);

        LocalTime randomTime = LocalTime.ofSecondOfDay(randomSeconds);
        return LocalDateTime.of(LocalDate.now(), randomTime);
    }

    public static boolean isNotificationWithinChallengePeriod(UserNotification notification, Challenge challenge) {
        LocalDateTime notificationTime = notification.getNotificationTime();
        LocalDateTime startDate = challenge.getStartDate();
        LocalDateTime endDate = challenge.getEndDate();

        return (notificationTime.isAfter(startDate) || notificationTime.isEqual(startDate)) &&
                (notificationTime.isBefore(endDate) || notificationTime.isEqual(endDate));
    }
}
