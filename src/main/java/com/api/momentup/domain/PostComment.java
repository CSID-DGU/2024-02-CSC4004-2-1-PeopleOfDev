package com.api.momentup.domain;


import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class PostComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postCommentNumber;
    private String commentContent;
    private LocalDateTime writeDate;

    @ManyToOne
    @JoinColumn(name = "post_number")
    private Post post;

    public static PostComment createPostComment(String commentContent, Post post) {
        PostComment postComment = new PostComment();
        postComment.commentContent = commentContent;
        postComment.post = post;
        postComment.writeDate = LocalDateTime.now();

        return postComment;
    }
}
