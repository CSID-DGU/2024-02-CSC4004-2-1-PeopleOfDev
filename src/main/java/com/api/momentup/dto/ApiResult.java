package com.api.momentup.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ApiResult<T> {
    private final int code;
    private final String message;
    private final T data;

    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(ResultType.OK.getCode(), ResultType.OK.getMessage(), data);
    }

    public static <T> ApiResult<T> error(int code, String message) {
        return new ApiResult<>(code, message, null);
    }
}
