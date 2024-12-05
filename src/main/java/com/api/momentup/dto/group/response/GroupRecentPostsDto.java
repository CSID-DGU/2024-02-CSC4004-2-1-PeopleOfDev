package com.api.momentup.dto.group.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GroupRecentPostsDto {
    private Long postNumber;
    private String writerId;
    private String postPicturePath;
}
