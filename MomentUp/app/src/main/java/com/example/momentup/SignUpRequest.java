package com.example.momentup;

public class SignUpRequest {
    private String name;
    private String username;
    private String password;
    private String email;

    public SignUpRequest(String name, String username, String password, String email) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.email = email;
    }
}