package com.api.momentup.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class CountDto {
    private int count;

    public CountDto(int count) {
        this.count = count;
    }
}
