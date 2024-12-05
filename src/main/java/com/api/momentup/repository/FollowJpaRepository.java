package com.api.momentup.repository;

import com.api.momentup.domain.Follow;
import com.api.momentup.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FollowJpaRepository extends JpaRepository<Follow, Long> {
    boolean existsByFollowerAndFollowing(Users follower, Users followed);

    Optional<Follow> findByFollowerAndFollowing(Users follower, Users followed);
}
