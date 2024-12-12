package com.example.momentup.api;

import java.util.Objects;

import lombok.Data;

@Data
public class ApiResult<T> {
    private int resultCode;
    private String message;
    private T data;

    public boolean isSuccessful() {
        return Objects.equals(message, "성공");
    }
}