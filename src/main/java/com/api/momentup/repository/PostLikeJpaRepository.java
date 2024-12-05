package com.api.momentup.repository;

import com.api.momentup.domain.Post;
import com.api.momentup.domain.PostLike;
import com.api.momentup.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeJpaRepository extends JpaRepository<PostLike, Long> {
    // 특정 게시물과 사용자의 좋아요 여부 확인
    Optional<PostLike> findByPost_PostNumberAndUser_UserNumber(Long postId, Long userId);

    // 특정 게시물의 총 좋아요 개수
    int countByPost_PostNumber(Long postId);
    boolean existsByPostAndUser(Post post, Users user);// 특정 사용자의 좋아요 여부
}
