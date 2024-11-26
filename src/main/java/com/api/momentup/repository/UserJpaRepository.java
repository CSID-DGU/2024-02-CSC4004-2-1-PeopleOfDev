package com.api.momentup.repository;

import com.api.momentup.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByUserId(String userId);
    Optional<Users> findByUserIdAndUserPw(String userId, String userPw);
    Optional<Users> findByUserNameAndUserEmail(String userName, String userEmail);
    Optional<Users> findByUserNameAndUserIdAndUserEmail(String userName, String userId, String userEmail);


}
