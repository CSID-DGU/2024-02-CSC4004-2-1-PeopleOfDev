package com.api.momentup.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class PostPicture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postPictureNumber;

    @OneToOne
    @JoinColumn(name = "post_number")
    private Post post;

    private String picturePath;
    private String pictureName;

    public void setPost(Post post) {
        this.post = post;
    }

    public static PostPicture createPostPicture(Post post, String picturePath, String pictureName) {
        PostPicture postPicture = new PostPicture();
        postPicture.post = post;
        postPicture.picturePath = picturePath;
        postPicture.pictureName = pictureName;

        return postPicture;
    }
}
