package com.api.momentup.service;

import com.api.momentup.domain.Groups;
import com.api.momentup.domain.Users;
import com.api.momentup.domain.UserGroups;
import com.api.momentup.repository.GroupJoinRequestJpaRepository;
import com.api.momentup.repository.GroupJpaRepository;
import com.api.momentup.repository.UserGroupJpaRepository;
import com.api.momentup.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GroupService {
    private final UserJpaRepository userJpaRepository;
    private final GroupJpaRepository groupJpaRepository;
    private final GroupJoinRequestJpaRepository groupJoinRequestJpaRepository;
    private final UserGroupJpaRepository userGroupJpaRepository;

    public void join(Long groupNumber, Long userNumber) {
        Optional<Users> findUser = userJpaRepository.findById(userNumber);
        Optional<Groups> findGroup = groupJpaRepository.findById(groupNumber);


        UserGroups userGroups = UserGroups.createUserGroup(findUser.get(), findGroup.get());
    }


    public void createGroup(String groupName, String hashTag, String groupIntro) {


    }
}
