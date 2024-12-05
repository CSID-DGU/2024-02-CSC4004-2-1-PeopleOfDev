package com.api.momentup.dto.post.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PostWriteRequest {
    @NotNull
    private String content;
    @NotNull
    private Long userNumber;

    private Long groupNumber;
}
