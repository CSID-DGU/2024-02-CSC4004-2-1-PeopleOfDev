package com.api.momentup.domain;


import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class PostComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postCommentNumber;
    private String commentContent;
    private LocalDateTime writeDate;

    @ManyToOne
    @JoinColumn(name = "user_number")
    private Users writer;

    @ManyToOne
    @JoinColumn(name = "post_number")
    private Post post;

    @OneToMany(mappedBy = "postComment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentLike> likes = new ArrayList<>();

    // 편의 메서드 추가
    public void addLike(CommentLike like) {
        likes.add(like);
        like.setPostComment(this);
    }

    public static PostComment createPostComment(Post post, String commentContent, Users writer) {
        PostComment postComment = new PostComment();
        postComment.commentContent = commentContent;
        postComment.post = post;
        postComment.writer = writer;
        postComment.writeDate = LocalDateTime.now();

        return postComment;
    }
}
