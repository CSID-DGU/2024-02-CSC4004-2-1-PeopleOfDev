package com.example.momentup;

import lombok.Data;

@Data
public class UserTokenRequest {
    private Long userNumber;
    private String fcmToken;

    public UserTokenRequest(Long userNumber, String fcmToken) {
        this.userNumber = userNumber;
        this.fcmToken = fcmToken;
    }
}
