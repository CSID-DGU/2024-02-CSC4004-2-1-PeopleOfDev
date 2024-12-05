package com.api.momentup.dto.user.response;

import lombok.Data;

@Data
public class FollowingsDto {
    private Long userNumber;
    private String userId;
    private String userProfilePath;

    public FollowingsDto(Long userNumber, String userId, String userProfilePath) {
        this.userNumber = userNumber;
        this.userId = userId;
        this.userProfilePath = userProfilePath;
    }
}
