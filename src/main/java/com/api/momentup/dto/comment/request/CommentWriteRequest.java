package com.api.momentup.dto.comment.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentWriteRequest {
    @NotNull
    private Long postNumber;
    @NotNull
    private String content;
    @NotNull
    private Long writeUserNumber;
}
