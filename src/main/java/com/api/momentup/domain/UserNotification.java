package com.api.momentup.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
public class UserNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationNumber;

    private String title;

    private String content;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @ManyToOne
    @JoinColumn(name = "user_number", nullable = false)
    private Users user;

    private Long targetNumber;
    private String userFcmToken;

    private LocalTime notificationTime;


    public static UserNotification createUserNotification(
            NotificationType notificationType,
            Long targetNumber,
            String title,
            String content,
            LocalTime  notificationTime,
            String userFcmToken,
            Users user) {
        UserNotification userNotification = new UserNotification();
        userNotification.notificationType = notificationType;
        userNotification.targetNumber = targetNumber;
        userNotification.title = title;
        userNotification.content = content;
        userNotification.userFcmToken = userFcmToken;
        userNotification.notificationTime = notificationTime;
        userNotification.user = user;

        return userNotification;
    }
}
