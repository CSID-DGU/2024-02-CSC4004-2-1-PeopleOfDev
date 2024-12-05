package com.api.momentup.dto.user.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String userId;
    private String userPw;
}
