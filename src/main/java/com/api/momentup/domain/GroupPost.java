package com.api.momentup.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class GroupPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupPostNumber;

    private String postContent;
    private String postPicture;
    private LocalDateTime writeDate;
    private int postLike;

    @ManyToOne
    @JoinColumn(name = "group_number")
    private Groups groups;

    @ManyToOne
    @JoinColumn(name = "user_number")
    private Users users;

    public static GroupPost createGroupPost(String postContent, String postPicture, Groups groups) {
        GroupPost groupPost = new GroupPost();
        groupPost.postContent = postContent;
        groupPost.postPicture = postPicture;
        groupPost.groups = groups;

        return groupPost;
    }
}
