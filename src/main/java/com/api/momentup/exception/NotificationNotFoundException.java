package com.api.momentup.exception;

public class NotificationNotFoundException extends Exception{
    public NotificationNotFoundException() {
        super("존재하지 않는 알림입니다.");
    }
}
