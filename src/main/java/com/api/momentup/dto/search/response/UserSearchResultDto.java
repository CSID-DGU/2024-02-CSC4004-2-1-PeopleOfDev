package com.api.momentup.dto.search.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserSearchResultDto {
    private Long userNumber;
    private String userId;
    private boolean isFollowed;
}
