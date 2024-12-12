package com.api.momentup.repository;

import com.api.momentup.domain.GroupJoinRequest;
import com.api.momentup.domain.Groups;
import com.api.momentup.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupJoinRequestJpaRepository extends JpaRepository<GroupJoinRequest, Long> {
    Optional<GroupJoinRequest> findByUsersAndGroups(Users users, Groups groups);

    List<GroupJoinRequest> findByGroups(Groups groups);
}
