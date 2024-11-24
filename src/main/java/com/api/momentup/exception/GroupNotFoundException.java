package com.api.momentup.exception;

public class GroupNotFoundException extends Exception{
    public GroupNotFoundException(String message) {
        super(message);  // 부모 클래스의 생성자 호출하여 메시지 전달
    }
}
