package com.api.momentup.exception;

public class AlreadyFollowException extends Exception {
    public AlreadyFollowException() {
        super("이미 팔로우 중입니다.");
    }
}
