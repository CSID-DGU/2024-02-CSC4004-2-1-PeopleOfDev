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


    @ManyToOne
    @JoinColumn(name = "user_number")
    private Users writer;

    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private PostPicture postPicture;

    @ManyToOne
    @JoinColumn(name = "group_number", nullable = true) // 그룹과의 관계 (nullable)
    private Groups group;

    @Enumerated(EnumType.STRING)
    private PostVisibility visibility; // 공개 범위

    public void setPostPicture(PostPicture postPicture) {
        this.postPicture = postPicture;
        postPicture.setPost(this);
    }

    public static Post createPost(String content, Users users, Groups groups) {
        Post post = new Post();
        post.content = content;
        post.writeDate = LocalDateTime.now();
        post.writer = users;
        post.group = groups;
        post.visibility = PostVisibility.GROUP;

        return post;
    }

    public static Post createPost(String content, Users users) {
        Post post = new Post();
        post.content = content;
        post.writeDate = LocalDateTime.now();
        post.writer = users;
        post.visibility = PostVisibility.PUBLIC;

        return post;
    }
}
