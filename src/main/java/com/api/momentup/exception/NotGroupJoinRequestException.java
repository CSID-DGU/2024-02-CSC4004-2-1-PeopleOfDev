package com.api.momentup.exception;

public class NotGroupJoinRequestException extends Exception {
    public NotGroupJoinRequestException() {
        super("그룹 가입 신청 내역이 없습니다.");
    }
}
