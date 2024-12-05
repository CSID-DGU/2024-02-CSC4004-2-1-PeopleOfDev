package com.api.momentup.exception;

import lombok.Data;


public class AleadyGroupJoinException extends Exception {
    public AleadyGroupJoinException() {
        super("이미 가입한 유저입니다.");
    }
}
