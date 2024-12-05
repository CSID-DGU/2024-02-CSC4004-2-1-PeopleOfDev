package com.api.momentup.repository;

import com.api.momentup.domain.Challenge;
import com.api.momentup.domain.Groups;
import com.api.momentup.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChallengeJpaRepository extends JpaRepository<Challenge, Long> {
    List<Challenge> findByUsersAndGroups(Users user, Groups group);

    List<Challenge> findByUsers(Users user);
}
