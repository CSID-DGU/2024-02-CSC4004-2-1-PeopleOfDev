package com.api.momentup.exception;

public class AlreadyGroupJoinRequestException extends Exception {
    public AlreadyGroupJoinRequestException() {
        super("이미 가입 신청한 상태입니다.");
    }
}
