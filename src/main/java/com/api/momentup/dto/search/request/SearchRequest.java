package com.api.momentup.dto.search.request;

import lombok.Data;

@Data
public class SearchRequest {
    private Long ownUserNumber;
    private String keyword;
}
