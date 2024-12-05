package com.api.momentup.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class PostLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_number")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_number")
    private Users user;



    public PostLike(Post post, Users user) {
        this.post = post;
        this.user = user;
    }
}
