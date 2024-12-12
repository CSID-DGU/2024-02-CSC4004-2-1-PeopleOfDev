package com.api.momentup.exception;

public class UserTokenNotFoundException extends Exception {
    public UserTokenNotFoundException() {
        super("등록된 유저 토큰이 없습니다.");
    }
}
