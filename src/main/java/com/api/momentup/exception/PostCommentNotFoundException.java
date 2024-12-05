package com.api.momentup.exception;

public class PostCommentNotFoundException extends Exception{
    public PostCommentNotFoundException() {
        super("댓글을 찾을 수 없습니다.");
    }
}
