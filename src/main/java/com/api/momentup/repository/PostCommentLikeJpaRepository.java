package com.api.momentup.repository;

import com.api.momentup.domain.CommentLike;
import com.api.momentup.domain.PostComment;
import com.api.momentup.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostCommentLikeJpaRepository extends JpaRepository<CommentLike, Long> {
    boolean existsByPostCommentAndUser(PostComment postComment, Users user);
    void deleteByPostCommentAndUser(PostComment postComment, Users user);
    long countByPostComment(PostComment postComment);
}
