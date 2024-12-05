package com.api.momentup.repository;

import com.api.momentup.domain.Post;
import com.api.momentup.domain.PostComment;
import com.api.momentup.dto.comment.response.CommentsDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostCommentJpaRepository extends JpaRepository<PostComment, Long> {
    List<PostComment> findByPost(Post post);
}
