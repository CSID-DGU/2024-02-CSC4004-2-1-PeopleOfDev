package com.api.momentup.repository;

import com.api.momentup.domain.Post;
import com.api.momentup.domain.UserGroups;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostJpaRepository extends JpaRepository<Post, Long> {
}
