package com.api.momentup.dto.post.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RecentPostsDto {
    private Long postNumber;
    private String userId;
    private String picturePath;
}
