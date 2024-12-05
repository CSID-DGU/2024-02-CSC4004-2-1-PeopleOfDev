package com.api.momentup.repository;

import com.api.momentup.dto.comment.response.CommentsDto;

import java.util.List;

public interface PostCommentQueryDslRepository {
    List<CommentsDto> findCommentsWithLikeInfoQueryDSL(Long postNumber, Long userNumber);
}
