package com.api.momentup.repository;

import com.api.momentup.domain.Post;
import com.api.momentup.domain.UserGroups;
import com.api.momentup.domain.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Query("SELECT p FROM Post p " +
            "LEFT JOIN PostLike pl ON p.postNumber = pl.post.postNumber " +
            "WHERE p.group.groupNumber = :groupNumber " +
            "GROUP BY p.postNumber " +
            "ORDER BY COUNT(pl.likeNumber) DESC, p.writeDate DESC")
    List<Post> findTopLikedPostsByGroup(@Param("groupNumber") Long groupNumber, Pageable pageable);

    @Query("SELECT p FROM Post p " +
            "WHERE p.group.groupNumber = :groupNumber AND p.postNumber NOT IN :excludedPostNumbers " +
            "ORDER BY p.writeDate DESC")
    List<Post> findLatestPostsByGroupExcluding(@Param("groupNumber") Long groupNumber,
                                               @Param("excludedPostNumbers") List<Long> excludedPostNumbers,
                                               Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.group.groupNumber = :groupNumber ORDER BY p.writeDate DESC")
    List<Post> findLatestPostsByGroup(@Param("groupNumber") Long groupNumber);

    @Query("SELECT p FROM Post p WHERE p.group.groupNumber = :groupNumber ORDER BY p.writeDate DESC")
    Page<Post> findLatestPostsByGroup(@Param("groupNumber") Long groupNumber, Pageable pageable);

    // 특정 유저의 특정 날짜에 작성된 게시물 조회
    @Query("SELECT p FROM Post p " +
            "WHERE p.writer.userNumber = :userNumber " +
            "AND p.writeDate BETWEEN :startOfDay AND :endOfDay " +
            "ORDER BY p.writeDate DESC")
    List<Post> findByWriterAndDate(
            @Param("userNumber") Long userNumber,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay);

    List<Post> findByWriterOrderByWriteDateDesc(Users users);
}
