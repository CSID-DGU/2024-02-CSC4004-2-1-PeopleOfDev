package com.api.momentup.dto.user.request;

import lombok.Data;

@Data
public class FollowRequest {
    private Long ownUserNumber;
    private Long targetUserNumber;
}
