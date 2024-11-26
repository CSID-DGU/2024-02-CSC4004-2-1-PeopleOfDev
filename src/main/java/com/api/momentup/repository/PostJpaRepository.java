package com.api.momentup.repository;

import com.api.momentup.domain.Post;
import com.api.momentup.domain.UserGroups;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostJpaRepository extends JpaRepository<Post, Long> {
    @Query("""
        SELECT p 
        FROM Post p
        WHERE p.writer IN (
            SELECT f.following 
            FROM Follow f 
            WHERE f.follower.userNumber = :userNumber
        )
        ORDER BY p.writeDate DESC
    """)
    List<Post> findLatestPostsByFollowedUsers(@Param("userNumber") Long userNumber);
}
