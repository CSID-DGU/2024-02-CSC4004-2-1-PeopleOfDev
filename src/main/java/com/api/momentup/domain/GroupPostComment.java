package com.api.momentup.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class GroupPostComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupCommentNumber;

    private String commentContent;
    private LocalDateTime writeDate;

    @ManyToOne
    @JoinColumn(name = "group_post_number")
    private GroupPost groupPost;

    public static GroupPostComment createGroupPostComment(String commentContent, GroupPost groupPost) {
        GroupPostComment groupPostComment = new GroupPostComment();
        groupPostComment.commentContent = commentContent;
        groupPostComment.groupPost = groupPost;
        groupPostComment.writeDate = LocalDateTime.now();

        return groupPostComment;
    }
}
