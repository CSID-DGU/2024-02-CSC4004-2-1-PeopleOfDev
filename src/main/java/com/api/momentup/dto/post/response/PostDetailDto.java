package com.api.momentup.dto.post.response;

import com.api.momentup.dto.comment.response.CommentsDto;
import lombok.Data;

import java.util.List;

@Data
public class PostDetailDto {
    private Long writerNumber;
    private String writerId;
    private String content;
    private String writerPicturePath;
    private String postPicturePath;
    private int likeCount;
    private boolean isLikeClick;
    private List<CommentsDto> comments;
}
