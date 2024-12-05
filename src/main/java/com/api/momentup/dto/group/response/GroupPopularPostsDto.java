package com.api.momentup.dto.group.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GroupPopularPostsDto {
    private Long postNumber;
    private String content;
    private String userId;
    private String postPicturePath;
}
