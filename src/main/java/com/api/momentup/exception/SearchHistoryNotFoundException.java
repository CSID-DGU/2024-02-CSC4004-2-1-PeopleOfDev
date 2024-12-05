package com.api.momentup.exception;

public class SearchHistoryNotFoundException extends Exception{
    public SearchHistoryNotFoundException() {
        super("해당하는 검색 기록이 존재하지 않습니다.");
    }
}
