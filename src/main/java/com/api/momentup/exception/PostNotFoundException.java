package com.api.momentup.exception;

public class PostNotFoundException extends Exception {
    public PostNotFoundException() {
        super("조회하려는 게시물이 존재하지 않습니다.");
    }
}
