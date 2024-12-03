package com.api.momentup.service;


import com.api.momentup.domain.*;
import com.api.momentup.dto.group.response.HomeGroupsDto;
import com.api.momentup.dto.user.response.FollowersDto;
import com.api.momentup.exception.GroupNotFoundException;
import com.api.momentup.exception.NotificationNotFoundException;
import com.api.momentup.exception.UserDuplicateException;
import com.api.momentup.exception.UserNotFoundException;
import com.api.momentup.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserJpaRepository userJpaRepository;
    private final GroupJpaRepository groupJpaRepository;
    private final GroupJoinRequestJpaRepository groupJoinRequestJpaRepository;
    private final UserProfileJpaRepository userProfileJpaRepository;
    private final FollowJpaRepository followJpaRepository;
    private final EmailService emailService;
    private final UserGroupJpaRepository userGroupJpaRepository;
    private final UserNotificationJpaRepository userNotificationJpaRepository;

    // 업로드 디렉토리 (application.properties에서 설정)
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Transactional
    public Long join(String userId, String userPw, String userName, String userEmail, MultipartFile file) throws Exception {
        System.out.println("user ID : "+ userId);
        Optional<Users> findUser = userJpaRepository.findByUserId(userId);

        if(findUser.isPresent()) {
            throw new UserDuplicateException();
        }

        String usbDir = "user";
        Long returnNumber = null;

        try {
            String originalFilename = file.getOriginalFilename();
            String filename = UUID.randomUUID() + "_" + originalFilename;

            // 파일 저장 경로 설정
            Path filePath = Paths.get(uploadDir, usbDir ,filename);
            Files.createDirectories(filePath.getParent()); // 디렉토리 생성
            Files.copy(file.getInputStream(), filePath); // 파일 저장

            String saveFilePath = "/uploaded/photos/"+ usbDir+ "/" + filename;

            UserProfile saveUserProfile = UserProfile.createUesrProfile(saveFilePath, originalFilename);
            Users users = Users.createMember(userId, userName, userPw, userEmail, saveUserProfile);

            userJpaRepository.save(users);

            returnNumber = users.getUserNumber();
        } catch (Exception e) {
            throw new Exception();
        }

        return returnNumber;
    }

    @Transactional
    public Long join(String userId, String userPw, String userName, String userEmail) throws UserDuplicateException {
        Optional<Users> findUser = userJpaRepository.findByUserId(userId);

        if(findUser.isPresent()) {
            throw new UserDuplicateException();
        }

        Users users = Users.createMember(userId, userName, userPw, userEmail);

        userJpaRepository.save(users);

        return users.getUserNumber();
    }

    public void requestGroupJoin(Long userNumber, Long groupNumber) throws UserNotFoundException, GroupNotFoundException {
        Users findUsers = userJpaRepository.findById(userNumber)
                .orElseThrow(UserNotFoundException::new);

        Groups findGroups = groupJpaRepository.findById(groupNumber)
                .orElseThrow(GroupNotFoundException::new);

        GroupJoinRequest groupJoinRequest = GroupJoinRequest.createGroupJoinRequest(findUsers, findGroups);
        groupJoinRequestJpaRepository.save(groupJoinRequest);
    }

    public Users login(String userId, String userPw) throws UserNotFoundException {
        Users findUsers = userJpaRepository.findByUserIdAndUserPw(userId, userPw)
                .orElseThrow(UserNotFoundException::new);

        return findUsers;
    }

    public void findUserId(String userName, String userEmail) throws UserNotFoundException {
        System.out.println("userName : "+ userName + "  userEmail : "+userEmail);
        Users findUser = userJpaRepository.findByUserNameAndUserEmail(userName, userEmail)
                .orElseThrow(UserNotFoundException::new);

        emailService.sendSimpleEmail(findUser.getUserEmail(), "Moment Up 아이디 찾기", "아이디는 "+ findUser.getUserId() + " 입니다.");

    }

    public void findUserPw(String userName, String userId, String userEmail) throws UserNotFoundException {
        System.out.println("userName : "+ userName + "  userEmail : "+userEmail);
        Users findUser = userJpaRepository.findByUserNameAndUserIdAndUserEmail(userName, userId, userEmail)
                .orElseThrow(UserNotFoundException::new);

        emailService.sendSimpleEmail(findUser.getUserEmail(), "Moment Up 비밀번호 찾기", "비밀번호는 "+ findUser.getUserPw() + " 입니다.");

    }

    // 팔로우 신청
    @Transactional
    public Long follow(Long userNotificationNumber, Long followerNumber, Long followingNumber) throws NotificationNotFoundException, UserNotFoundException {
        Users follower = userJpaRepository.findById(followerNumber)
                .orElseThrow(UserNotFoundException::new);
        Users following = userJpaRepository.findById(followingNumber)
                .orElseThrow(UserNotFoundException::new);

        // 중복 팔로우 방지
        boolean alreadyFollowing = followJpaRepository.existsByFollowerAndFollowing(follower, following);
        if (alreadyFollowing) {
            throw new IllegalStateException("이미 팔로우 중입니다.");
        }

        Follow follow = Follow.builder()
                .follower(follower)
                .following(following)
                .build();

        followJpaRepository.save(follow);

        UserNotification findNotification = userNotificationJpaRepository.findById(userNotificationNumber)
                .orElseThrow(NotificationNotFoundException::new);

        userNotificationJpaRepository.delete(findNotification);

        return follow.getFollowNumber();
    }

    @Transactional
    public Long requestFollow(Long ownUserNumber, Long targetUserNumber) throws UserNotFoundException {
        Users findUser = userJpaRepository.findById(ownUserNumber)
                .orElseThrow(UserNotFoundException::new);
        String title = "팔로우 신청";
        String content = findUser.getUserId() + "님이 팔로우를 요청하였습니다.";
        LocalTime notificationTime = LocalTime.now().plusMinutes(1);
        UserNotification saveNotification = UserNotification.createUserNotification(NotificationType.FOLLOW,
                targetUserNumber, title, content, notificationTime, "", findUser);
        userNotificationJpaRepository.save(saveNotification);

        return saveNotification.getNotificationNumber();
    }

    @Transactional
    public void requestRejectFollow(Long userNotificationNumber) throws NotificationNotFoundException {
        UserNotification findNotification = userNotificationJpaRepository.findById(userNotificationNumber)
                .orElseThrow(NotificationNotFoundException::new);

        userNotificationJpaRepository.delete(findNotification);
    }

    @Transactional
    public void unfollow(Long followerId, Long followedId) throws UserNotFoundException {
        Users follower = userJpaRepository.findById(followerId)
                .orElseThrow(UserNotFoundException::new);
        Users followed = userJpaRepository.findById(followedId)
                .orElseThrow(UserNotFoundException::new);

        Follow follow = followJpaRepository.findByFollowerAndFollowing(follower, followed)
                .orElseThrow(UserNotFoundException::new);

        followJpaRepository.delete(follow);
    }

    public List<Users> getFollowingList(Long userId) {
        Users user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저 ID입니다."));
        return user.getFollowing().stream()
                .map(Follow::getFollowing) // 팔로우한 대상만 추출
                .collect(Collectors.toList());
    }


    public List<FollowersDto> getFollowersListWithMutualStatus(Long userId) {
        Users user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저 ID입니다."));

        List<Users> allFollowers = user.getFollowers().stream()
                .map(Follow::getFollower)
                .collect(Collectors.toList());

        List<FollowersDto> followers = new ArrayList<>();

        for (Users follower : allFollowers) {
            boolean isMutualFollow = user.getFollowing().stream()
                    .anyMatch(f -> f.getFollowing().equals(follower));

            String userProfilePath = "";

            if(follower.getUserProfile() != null) {
                userProfilePath = follower.getUserProfile().getPicturePath();
            }

            followers.add(new FollowersDto(follower.getUserNumber(), follower.getUserId(),
                    userProfilePath , isMutualFollow));
        }

        return followers;
    }

    public List<HomeGroupsDto> getUserGroups(Long userNumber) throws UserNotFoundException {
        Users findUser = userJpaRepository.findById(userNumber)
                .orElseThrow(UserNotFoundException::new);

        List<HomeGroupsDto> groupsByUserNumber = userGroupJpaRepository.findGroupsWithLastPostTime(userNumber);

        return groupsByUserNumber;
    }


}
