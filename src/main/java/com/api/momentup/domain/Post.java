package com.api.momentup.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postNumber;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;
    private LocalDateTime writeDate;
    private int postLike;

    @ManyToOne
    @JoinColumn(name = "user_number")
    private Users writer;

    public static Post createPost(String content, String postPicture, Users users) {
        Post post = new Post();
        post.content = content;
        post.writeDate = LocalDateTime.now();
        post.postLike = 0;
        post.writer = users;

        return post;
    }
}
