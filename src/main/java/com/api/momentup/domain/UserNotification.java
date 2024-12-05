package com.api.momentup.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
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

    @ManyToOne
    @JoinColumn(name = "group_number", nullable = true)
    private Groups group;

    private Long targetNumber;

    private LocalDateTime notificationTime;


    public static UserNotification createUserNotification(
            NotificationType notificationType,
            Long targetNumber,
            String title,
            String content,
            Users user,
            Groups group) {
        UserNotification userNotification = new UserNotification();
        userNotification.notificationType = notificationType;
        userNotification.targetNumber = targetNumber;
        userNotification.title = title;
        userNotification.content = content;
        userNotification.notificationTime = LocalDateTime.now();
        userNotification.user = user;
        userNotification.group = group;

        return userNotification;
    }
}
