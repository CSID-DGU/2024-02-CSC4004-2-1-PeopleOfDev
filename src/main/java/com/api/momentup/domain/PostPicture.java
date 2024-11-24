package com.api.momentup.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class PostPicture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postPictureNumber;

    @ManyToOne
    @JoinColumn(name = "post_number")
    private Post post;

    private String picturePath;

    public static PostPicture createPostPicture(Post post, String picturePath) {
        PostPicture postPicture = new PostPicture();
        postPicture.post = post;
        postPicture.picturePath = picturePath;

        return postPicture;
    }
}
