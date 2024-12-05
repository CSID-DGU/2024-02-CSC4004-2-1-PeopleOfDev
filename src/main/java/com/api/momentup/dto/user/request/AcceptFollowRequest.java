package com.api.momentup.dto.user.request;

import lombok.Data;

@Data
public class AcceptFollowRequest {
    private Long userNotificationNumber;
    private Long followNumber;
    private Long followingNumber;
}
