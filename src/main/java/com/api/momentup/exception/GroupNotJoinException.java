package com.api.momentup.exception;

public class GroupNotJoinException extends Exception{
    public GroupNotJoinException() {
        super("가입한 그룹이 아닙니다.");
    }
}
