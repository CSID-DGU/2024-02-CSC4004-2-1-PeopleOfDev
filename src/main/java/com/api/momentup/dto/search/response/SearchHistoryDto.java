package com.api.momentup.dto.search.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SearchHistoryDto {
    private Long searchHistoryNumber;
    private String keyword;
}
