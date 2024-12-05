package com.api.momentup.dto.user.request;

import lombok.Data;

@Data
public class UserTokenRequest {
    private Long userNumber;
    private String fcmToken;
}
