package com.example.momentup.model;

public class Group {
    private Long groupNumber;
    private String groupName;
    private String hashTag;
    private String groupIntro;
    private String groupPicturePath;
    private Long userNumber;

    // Required getters
    public Long getGroupNumber() {
        return groupNumber;
    }

    public String getGroupName() {
        return groupName;
    }

    // Required setters
    public void setGroupNumber(Long groupNumber) {
        this.groupNumber = groupNumber;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    // Other getters and setters
    public String getHashTag() {
        return hashTag;
    }

    public void setHashTag(String hashTag) {
        this.hashTag = hashTag;
    }

    public String getGroupIntro() {
        return groupIntro;
    }

    public void setGroupIntro(String groupIntro) {
        this.groupIntro = groupIntro;
    }

    public String getGroupPicturePath() {
        return groupPicturePath;
    }

    public void setGroupPicturePath(String groupPicturePath) {
        this.groupPicturePath = groupPicturePath;
    }

    public Long getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(Long userNumber) {
        this.userNumber = userNumber;
    }
}