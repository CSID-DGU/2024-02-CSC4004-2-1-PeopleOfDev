package com.api.momentup.domain;

import jakarta.persistence.*;

@Entity
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long followNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_number")
    private Users follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followed_id")
    private Users followed;

    public Follow(Users follower, Users followed) {
        this.follower = follower;
        this.followed = followed;
    }

}
