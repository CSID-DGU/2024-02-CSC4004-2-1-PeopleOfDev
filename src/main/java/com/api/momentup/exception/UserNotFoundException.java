package com.api.momentup.exception;

public class UserNotFoundException extends Exception {
    // 예외 메시지와 원인 예외를 전달하는 생성자
    public UserNotFoundException(String message) {
        super(message);  // 부모 클래스의 생성자 호출하여 메시지 전달
    }

    // 예외 메시지와 원인 예외를 전달하는 생성자 (원인 예외 포함)
    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);  // 부모 클래스의 생성자 호출
    }
}
