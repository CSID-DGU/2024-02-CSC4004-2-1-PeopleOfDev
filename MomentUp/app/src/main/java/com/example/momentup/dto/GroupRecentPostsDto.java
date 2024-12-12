package com.example.momentup.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupRecentPostsDto {
    private Long postNumber;
    private String content;
    private String createdAt;
    private String userName;
    private String userProfile;
    private String picturePath;  // Added for image loading
    private int likeCount;
    private int commentCount;

    // Getter for picturePath
    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }
}