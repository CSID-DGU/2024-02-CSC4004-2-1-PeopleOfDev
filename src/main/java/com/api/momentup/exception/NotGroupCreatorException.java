package com.api.momentup.exception;

public class NotGroupCreatorException extends Exception{
    public NotGroupCreatorException() {
        super("그룹 생성자만 가능한 기능입니다.");
    }
}
