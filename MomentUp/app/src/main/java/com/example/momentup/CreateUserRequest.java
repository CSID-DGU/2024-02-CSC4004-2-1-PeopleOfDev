package com.example.momentup;

import lombok.Data;

@Data
public class CreateUserRequest {
    private String userId;
    private String userPw;
    private String userName;
    private String userEmail;

    public CreateUserRequest(String userId, String userPw, String userName, String userEmail) {
        this.userId = userId;
        this.userPw = userPw;
        this.userName = userName;
        this.userEmail = userEmail;
    }
}
