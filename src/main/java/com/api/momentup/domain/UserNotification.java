package com.api.momentup.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class UserNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationNumber;

    private String content;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @ManyToOne
    @JoinColumn(name = "user_number", nullable = false)
    private Users user;

    private Long targetNumber;


    public static UserNotification createUserNotification(NotificationType notificationType, Long targetNumber, String content, Users user) {
        UserNotification userNotification = new UserNotification();
        userNotification.notificationType = notificationType;
        userNotification.targetNumber = targetNumber;
        userNotification.content = content;
        userNotification.user = user;

        return userNotification;
    }
}
