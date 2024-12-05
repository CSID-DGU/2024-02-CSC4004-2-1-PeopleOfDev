package com.api.momentup.dto.user.response;

import lombok.Data;

@Data
public class FollowersDto {
    private Long userNumber;
    private String userId;
    private String userProfilePath;
    private boolean isMutualFollow;

    public FollowersDto(Long userNumber, String userId, String userProfilePath, boolean isMutualFollow) {
        this.userNumber = userNumber;
        this.userId = userId;
        this.userProfilePath = userProfilePath;
        this.isMutualFollow = isMutualFollow;
    }
}
