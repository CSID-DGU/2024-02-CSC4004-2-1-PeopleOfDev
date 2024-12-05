package com.api.momentup.service;

import com.api.momentup.domain.*;
import com.api.momentup.exception.*;
import com.api.momentup.repository.GroupJoinRequestJpaRepository;
import com.api.momentup.repository.GroupJpaRepository;
import com.api.momentup.repository.UserGroupJpaRepository;
import com.api.momentup.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GroupService {
    private final UserJpaRepository userJpaRepository;
    private final GroupJpaRepository groupJpaRepository;
    private final GroupJoinRequestJpaRepository groupJoinRequestJpaRepository;
    private final UserGroupJpaRepository userGroupJpaRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public Groups getGroup(Long groupNumber) throws GroupNotFoundException {
        Groups findGroups = groupJpaRepository.findById(groupNumber)
                .orElseThrow(GroupNotFoundException::new);

        return findGroups;
    }

    @Transactional
    public Long join(Long groupNumber, Long userNumber) throws UserNotFoundException, GroupNotFoundException, NotGroupJoinRequestException, AleadyGroupJoinException {
        Users user = userJpaRepository.findById(userNumber)
                .orElseThrow(UserNotFoundException::new);
        Groups group = groupJpaRepository.findById(groupNumber)
                .orElseThrow((UserNotFoundException::new));

        GroupJoinRequest findGroupJoinRequest = groupJoinRequestJpaRepository.findByUsersAndGroups(user, group)
                .orElseThrow(NotGroupJoinRequestException::new);

        // 중복 가입 방지 로직 추가
        boolean alreadyJoined = userGroupJpaRepository.existsByUsersAndGroups(user, group);
        if (alreadyJoined) {
            throw new AleadyGroupJoinException();
        }

        // UserGroups 생성 및 저장
        UserGroups userGroups = UserGroups.createUserGroup(user, group);
        userGroupJpaRepository.save(userGroups);

        groupJoinRequestJpaRepository.delete(findGroupJoinRequest);

        return userGroups.getUserGroupNumber();
    }

    @Transactional
    public Long createGroup(String groupName, String hashTag, String groupIntro, Long userNumber) throws UserNotFoundException {
        Users user = userJpaRepository.findById(userNumber)
                .orElseThrow(UserNotFoundException::new);

        Groups group = Groups.createGroup(groupName, hashTag, groupIntro, user);

        groupJpaRepository.save(group);

        return group.getGroupNumber();
    }

    @Transactional
    public Long createGroup(String groupName, String hashTag, String groupIntro, MultipartFile groupPicture, Long userNumber) throws Exception {
        Users user = userJpaRepository.findById(userNumber)
                .orElseThrow(UserNotFoundException::new);

        Groups group = Groups.createGroup(groupName, hashTag, groupIntro, user);
        String usbDir = "group";

        try {
            String originalFilename = groupPicture.getOriginalFilename();
            String filename = UUID.randomUUID() + "_" + originalFilename;

            // 파일 저장 경로 설정
            Path filePath = Paths.get(uploadDir, usbDir ,filename);
            Files.createDirectories(filePath.getParent()); // 디렉토리 생성
            Files.copy(groupPicture.getInputStream(), filePath); // 파일 저장

            String saveFilePath = "/uploaded/photos/"+ usbDir+ "/" + filename;

            GroupPicture saveGroupPicture = GroupPicture.createGroupPicture(group, saveFilePath, originalFilename);
            group.setGroupPicture(saveGroupPicture);

            groupJpaRepository.save(group);
        } catch (Exception e) {
            throw new Exception();
        }

        return group.getGroupNumber();
    }

    @Transactional
    public Long requestInviteCodeGroupJoin(Long userNumber, String inviteCode)
            throws UserNotFoundException, GroupNotFoundException, AlreadyGroupJoinRequestException {
        Users findUsers = userJpaRepository.findById(userNumber)
                .orElseThrow(UserNotFoundException::new);

        Groups findGroups = groupJpaRepository.findByGroupInviteCode(inviteCode)
                .orElseThrow((UserNotFoundException::new));

        Optional<GroupJoinRequest> findByRequest = groupJoinRequestJpaRepository.findByUsersAndGroups(findUsers, findGroups);

        if(findByRequest.isPresent()) {
            throw new AlreadyGroupJoinRequestException();
        }

        GroupJoinRequest saveGroupJoinRequest = GroupJoinRequest.createGroupJoinRequest(findUsers, findGroups);
        groupJoinRequestJpaRepository.save(saveGroupJoinRequest);

        return saveGroupJoinRequest.getRequestNumber();
    }

    public List<Users> getGroupJoinUsers(Long userNumber) {
        return userGroupJpaRepository.findByGroups_GroupNumber(userNumber)
                .stream()
                .map(UserGroups::getUsers)
                .collect(Collectors.toList());
    }

    @Transactional
    public void rejectGroupJoinRequest(Long groupJoinRequestNumber) throws NotGroupJoinRequestException {
        GroupJoinRequest findRequest = groupJoinRequestJpaRepository.findById(groupJoinRequestNumber)
                .orElseThrow(NotGroupJoinRequestException::new);

        groupJoinRequestJpaRepository.delete(findRequest);
    }

    public List<Groups> getUserJoinGroups(Long userNumber) {
        List<Groups> findGroups = userGroupJpaRepository.findGroupsByUserNumber(userNumber);

        return findGroups;
    }

    @Transactional
    public void expelGroupUser(Long groupNumber, Long userNumber) throws GroupNotFoundException, UserNotFoundException, GroupNotJoinException {
        Groups findGroups = groupJpaRepository.findById(groupNumber)
                .orElseThrow(GroupNotFoundException::new);

        Users findUsers = userJpaRepository.findById(userNumber)
                .orElseThrow(UserNotFoundException::new);

        UserGroups findUserGroups = userGroupJpaRepository.findByGroupsAndUsers(findGroups, findUsers)
                .orElseThrow(GroupNotJoinException::new);

        userGroupJpaRepository.delete(findUserGroups);
    }

}
