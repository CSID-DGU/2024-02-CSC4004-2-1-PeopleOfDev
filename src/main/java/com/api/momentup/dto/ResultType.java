package com.api.momentup.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResultType {
    OK(200, "성공"),
    FAIL(400, "실패"),
    NOT_FOUND(404, ""),;

    private final int code;
    private final String message;
}
