package com.api.momentup.service;


import com.api.momentup.domain.Groups;
import com.api.momentup.domain.GroupJoinRequest;
import com.api.momentup.domain.Users;
import com.api.momentup.domain.UserProfile;
import com.api.momentup.exception.GroupNotFoundException;
import com.api.momentup.exception.UserNotFoundException;
import com.api.momentup.repository.GroupJoinRequestJpaRepository;
import com.api.momentup.repository.GroupJpaRepository;
import com.api.momentup.repository.UserJpaRepository;
import com.api.momentup.repository.UserProfileJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserJpaRepository userJpaRepository;
    private final GroupJpaRepository groupJpaRepository;
    private final GroupJoinRequestJpaRepository groupJoinRequestJpaRepository;
    private final UserProfileJpaRepository userProfileJpaRepository;
    private final EmailService emailService;

    // 업로드 디렉토리 (application.properties에서 설정)
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Transactional
    public Long join(String userId, String userPw, String userName, String userEmail, MultipartFile file) throws Exception {
        Users users = Users.createMember(userId, userName, userPw, userEmail);
        String usbDir = "user";
        userJpaRepository.save(users);

        try {
            String originalFilename = file.getOriginalFilename();
            String filename = UUID.randomUUID() + "_" + originalFilename;

            // 파일 저장 경로 설정
            Path filePath = Paths.get(uploadDir, usbDir ,filename);
            Files.createDirectories(filePath.getParent()); // 디렉토리 생성
            Files.copy(file.getInputStream(), filePath); // 파일 저장

            String saveFilePath = "/uploaded/photos/"+ usbDir+ "/" + filename;

            UserProfile saveUserProfile = UserProfile.createUesrProfile(users, saveFilePath, originalFilename);

            users.setUserProfile(saveUserProfile);
            userJpaRepository.save(users);
        } catch (Exception e) {
            throw new Exception();
        }

        return users.getUserNumber();
    }

    @Transactional
    public Long join(String userId, String userPw, String userName, String userEmail) {
        Users users = Users.createMember(userId, userName, userPw, userEmail);

        userJpaRepository.save(users);

        return users.getUserNumber();
    }

    public void requestGroupJoin(Long userNumber, Long groupNumber) throws UserNotFoundException, GroupNotFoundException {
        Users findUsers = userJpaRepository.findById(userNumber)
                .orElseThrow(()-> new UserNotFoundException("User not found"));

        Groups findGroups = groupJpaRepository.findById(groupNumber)
                .orElseThrow(()-> new GroupNotFoundException("Group not found"));

        GroupJoinRequest groupJoinRequest = GroupJoinRequest.createGroupJoinRequest(findUsers, findGroups);
        groupJoinRequestJpaRepository.save(groupJoinRequest);
    }

    public Users login(String userId, String userPw) throws UserNotFoundException {
        Users findUsers = userJpaRepository.findByUserIdAndUserPw(userId, userPw)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return findUsers;
    }

    public void findUserId(String userName, String userEmail) throws UserNotFoundException {
        System.out.println("userName : "+ userName + "  userEmail : "+userEmail);
        Users findUser = userJpaRepository.findByUserNameAndUserEmail(userName, userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        emailService.sendSimpleEmail(findUser.getUserEmail(), "Moment Up 아이디 찾기", "아이디는 "+ findUser.getUserId() + " 입니다.");

    }

    public void findUserPw(String userName, String userId, String userEmail) throws UserNotFoundException {
        System.out.println("userName : "+ userName + "  userEmail : "+userEmail);
        Users findUser = userJpaRepository.findByUserNameAndUserIdAndUserEmail(userName, userId, userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        emailService.sendSimpleEmail(findUser.getUserEmail(), "Moment Up 비밀번호 찾기", "비밀번호는 "+ findUser.getUserPw() + " 입니다.");

    }
}
