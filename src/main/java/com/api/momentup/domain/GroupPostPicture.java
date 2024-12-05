package com.api.momentup.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class GroupPostPicture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupPostPictureNumber;

    @ManyToOne
    @JoinColumn(name = "group_post_number")
    private GroupPost groupPost;

    private String picturePath;

    public static GroupPostPicture createGroupPostPicture(GroupPost groupPost, String picturePath) {
        GroupPostPicture groupPostPicture = new GroupPostPicture();
        groupPostPicture.groupPost = groupPost;
        groupPostPicture.picturePath = picturePath;

        return groupPostPicture;
    }
}
