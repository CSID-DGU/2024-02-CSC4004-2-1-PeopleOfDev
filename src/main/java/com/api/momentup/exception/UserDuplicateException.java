package com.api.momentup.exception;

public class UserDuplicateException extends Exception{
    public UserDuplicateException() {
        super("중복된 ID가 존재합니다.");
    }
}
