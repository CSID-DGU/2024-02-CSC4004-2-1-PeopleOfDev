package com.api.momentup.repository;

import com.api.momentup.domain.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileJpaRepository extends JpaRepository<UserProfile, Long> {
}
