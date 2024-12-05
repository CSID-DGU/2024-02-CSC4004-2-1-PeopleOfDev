package com.api.momentup.dto.user;

import lombok.Data;

@Data
public class FindUserPwRequest {
    private String userName;
    private String userId;
    private String userEmail;
}
