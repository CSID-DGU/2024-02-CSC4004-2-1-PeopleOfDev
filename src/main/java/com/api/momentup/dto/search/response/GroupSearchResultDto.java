package com.api.momentup.dto.search.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GroupSearchResultDto {
    private Long groupNumber;
    private String groupName;
    private long memberCount;
}
