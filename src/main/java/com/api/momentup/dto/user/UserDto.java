package com.api.momentup.dto.user;

import com.api.momentup.domain.Users;
import lombok.Data;

@Data
public class UserDto {
    private Long userNumber;
    private String userId;
    private String userName;
    private String userEmail;
    private String userProfile;

    public UserDto(Users users) {
        this.userNumber = users.getUserNumber();
        this.userId = users.getUserId();
        this.userName = users.getUserName();
        this.userEmail = users.getUserEmail();
        this.userProfile = users.getUserProfile().getPicturePath();
    }
}
