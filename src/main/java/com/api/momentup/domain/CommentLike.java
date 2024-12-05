package com.api.momentup.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
public class CommentLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentLikeNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_comment_number")
    private PostComment postComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_number")
    private Users user;

    public void setPostComment(PostComment postComment) {
        this.postComment = postComment;
    }

    public static CommentLike createCommentLike(Users user) {
        CommentLike commentLike = new CommentLike();
        commentLike.user = user;

        return commentLike;
    }
}
