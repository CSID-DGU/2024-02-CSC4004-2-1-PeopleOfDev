package com.api.momentup.exception;

public class AlarmNotFoundException extends Exception{
    public AlarmNotFoundException() {
        super("조회하려는 알림 스케쥴이 존재하지 않습니다.");
    }
}
