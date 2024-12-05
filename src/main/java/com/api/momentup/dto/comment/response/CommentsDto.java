package com.api.momentup.dto.comment.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentsDto {
    private Long postCommentNumber;
    private String commentContent;
    private LocalDateTime writeDate;
    private String writerUserId;
    private String writerProfilePicturePath;
    private Long likeCount;
    private Boolean isLikedByUser;
}
