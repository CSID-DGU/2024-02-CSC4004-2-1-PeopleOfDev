package com.example.momentup;

public class LoginResponse {
    private String token;
    private boolean success;
    private String message;

    // getter methods
    public String getToken() { return token; }
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
}
