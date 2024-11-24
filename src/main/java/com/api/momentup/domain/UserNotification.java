package com.api.momentup.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class UserNotification {
    @Id
    @GeneratedValue
    private Long notificationNumber;

    private String content;
    private NotificationType type;

    private Long targetNumber;


    public static UserNotification createUserNotification(NotificationType type, Long targetNumber, String content) {
        UserNotification userNotification = new UserNotification();
        userNotification.type = type;
        userNotification.targetNumber = targetNumber;
        userNotification.content = content;

        return userNotification;
    }
}
