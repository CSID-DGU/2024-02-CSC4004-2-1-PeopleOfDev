package com.api.momentup.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class UserToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userTokenNumber;

    @ManyToOne
    @JoinColumn(name = "user_number", nullable = false)
    private Users users;

    @Column(nullable = false)
    private String fcmToken;
    private LocalDateTime lastUpdated;

    public void setUsers(Users users) {
        this.users = users;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public static UserToken createUserToken(Users users, String fcmToken) {
        UserToken userToken = new UserToken();
        userToken.users = users;
        userToken.fcmToken = fcmToken;
        userToken.lastUpdated = LocalDateTime.now();

        return userToken;
    }
}
