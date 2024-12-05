package com.api.momentup.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long followNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_number", nullable = false)
    private Users follower; // 팔로우를 하는 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followed_number", nullable = false)
    private Users following; // 팔로우를 당한 사용자

    @Builder
    public Follow(Users follower, Users following) {
        this.follower = follower;
        this.following = following;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Follow follow = (Follow) o;
        return Objects.equals(follower, follow.follower) &&
                Objects.equals(following, follow.following);
    }

    @Override
    public int hashCode() {
        return Objects.hash(follower, following);
    }

}
