package com.api.momentup.repository;

import com.api.momentup.domain.UserToken;
import com.api.momentup.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserTokenJpaRepository extends JpaRepository<UserToken, Long> {
    List<UserToken> findByUsers(Users user);
    Optional<UserToken> findByUsersAndFcmToken(Users user, String fcmToken);


}
