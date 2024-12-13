package com.api.momentup.api;

import com.api.momentup.domain.Calendar;
import com.api.momentup.domain.Post;
import com.api.momentup.domain.UserProfile;
import com.api.momentup.dto.ApiResult;
import com.api.momentup.dto.ResultType;
import com.api.momentup.dto.group.response.HomeGroupsDto;
import com.api.momentup.domain.Users;
import com.api.momentup.dto.post.request.GetDatePostRequest;
import com.api.momentup.dto.user.request.*;
import com.api.momentup.dto.user.response.*;
import com.api.momentup.exception.AlreadyFollowException;
import com.api.momentup.exception.NotificationNotFoundException;
import com.api.momentup.exception.UserDuplicateException;
import com.api.momentup.exception.UserNotFoundException;
import com.api.momentup.service.CalendarService;
import com.api.momentup.service.PostService;
import com.api.momentup.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User API", description = "사용자 관련 API")
public class UserApiController {
    private final UserService userService;
    private final CalendarService calendarService;
    private final PostService postService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "회원가입", description = "User 회원가입 API")
    public ApiResult saveUser(@RequestPart("user") CreateUserRequest request, @RequestPart(value = "profile", required = false) MultipartFile userProfile) {
        Long userNumber = null;
        try {
            if (userProfile == null || userProfile.isEmpty()) {
                System.out.println("userProfile is Empty");

                userNumber = userService.join(request.getUserId(), request.getUserPw(),
                        request.getUserName(), request.getUserEmail());
            } else {
                userNumber = userService.join(request.getUserId(), request.getUserPw(),
                        request.getUserName(), request.getUserEmail(), userProfile);
            }
        } catch (UserDuplicateException e) {
            System.out.println("UserDuplicateException ");
            return ApiResult.error(ResultType.FAIL.getCode(), e.getMessage());
        } catch (Exception e) {
            return ApiResult.error(ResultType.FAIL.getCode(), e.getMessage());
        }

        return ApiResult.success(userNumber);
    }

    // 유저 정보
    @GetMapping("/{userNumber}")
    public ApiResult getUser(@PathVariable Long postNumber, @RequestBody @Valid Long userNumber) {

        return ApiResult.success(null);
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "아이디 패스워드로 로그인")
    public ApiResult login(@RequestBody @Valid LoginRequest request) {
        try {
            Users users = userService.login(request.getUserId(), request.getUserPw());

            System.out.println("user id : " + users.getUserId());

            return ApiResult.success(new UserDto(users));
        } catch (UserNotFoundException e) {
            return ApiResult.error(ResultType.NOT_FOUND.getCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResult.error(ResultType.FAIL.getCode(), ResultType.FAIL.getMessage());
        }
    }


    // 아이디 찾기
    @PostMapping("/id")
    public ApiResult findUserId(@RequestBody FindUserIdRequest request) {
        try {
            userService.findUserId(request.getUserName(), request.getUserEmail());

            return ApiResult.success(null);
        } catch (UserNotFoundException e) {
            return ApiResult.error(ResultType.NOT_FOUND.getCode(), e.getMessage());
        } catch (Exception e) {
            return ApiResult.error(ResultType.FAIL.getCode(), ResultType.FAIL.getMessage());
        }
    }

    // 비밀번호 찾기
    @PostMapping("/password")
    public ApiResult findUserPw(@RequestBody FindUserPwRequest request) {
        try {
            userService.findUserPw(request.getUserName(), request.getUserId(), request.getUserEmail());

            return ApiResult.success(null);
        } catch (UserNotFoundException e) {
            return ApiResult.error(ResultType.NOT_FOUND.getCode(), e.getMessage());
        } catch (Exception e) {
            return ApiResult.error(ResultType.FAIL.getCode(), ResultType.FAIL.getMessage());
        }
    }

    // 홈 화면 그룹 목록 빛 최근 게시물 시간
    @GetMapping("/groups/{userNumber}")
    public ApiResult getUserGroups(@PathVariable Long userNumber) {
        try {
            List<HomeGroupsDto> findGroups = userService.getUserGroups(userNumber);

            return ApiResult.success(findGroups);
        } catch (UserNotFoundException e) {
            return ApiResult.error(ResultType.NOT_FOUND.getCode(), e.getMessage());
        }

    }

    // 팔로우 요청
    @PostMapping("/follow/request")
    public ApiResult requestFollow(@RequestBody FollowRequest request) {
        try {
            Long notificationNumber = userService.requestFollow(request.getOwnUserNumber(), request.getTargetUserNumber());

            return ApiResult.success(notificationNumber);
        } catch (UserNotFoundException e) {
            return ApiResult.error(ResultType.NOT_FOUND.getCode(), e.getMessage());
        } catch (AlreadyFollowException e) {
            return ApiResult.error(ResultType.FAIL.getCode(), e.getMessage());
        }
    }

    // 팔로우 수락
    @PostMapping("/follow")
    public ApiResult acceptFollow(@RequestBody AcceptFollowRequest request) {
        try {
            Long followNumber = userService.follow(
                    request.getUserNotificationNumber(), request.getFollowNumber(), request.getFollowingNumber()
            );

            return ApiResult.success(followNumber);
        } catch (NotificationNotFoundException e) {
            return ApiResult.error(ResultType.NOT_FOUND.getCode(), e.getMessage());
        } catch (UserNotFoundException e) {
            return ApiResult.error(ResultType.NOT_FOUND.getCode(), e.getMessage());
        } catch (AlreadyFollowException e) {
            return ApiResult.error(ResultType.FAIL.getCode(), e.getMessage());
        }
    }

    // 팔로우 요청 거절
   @DeleteMapping("/follow/request/{userNotificationNumber}")
    public ApiResult requestRejectFollow(@PathVariable Long userNotificationNumber) {
        try {
            userService.requestRejectFollow(userNotificationNumber);

            return ApiResult.success(null);
        }  catch (NotificationNotFoundException e) {
            return ApiResult.error(ResultType.NOT_FOUND.getCode(), e.getMessage());
        }
    }

    // 언팔로우
    @DeleteMapping("/follow")
    public ApiResult unFollow(@RequestBody FollowRequest request) {
        try {
            userService.unfollow(request.getTargetUserNumber(), request.getOwnUserNumber());

            return ApiResult.success(null);
        }  catch (UserNotFoundException e) {
            return ApiResult.error(ResultType.NOT_FOUND.getCode(), e.getMessage());
        }
    }

    // 팔로우 목록
    @GetMapping("/follow/{userNumber}")
    public ApiResult getFollows(@PathVariable Long userNumber) {
        try {
            List<FollowersDto> followersList = userService.getFollowersListWithMutualStatus(userNumber);

            return ApiResult.success(followersList);
        } catch (Exception e) {
            return ApiResult.error(ResultType.FAIL.getCode(), ResultType.FAIL.getMessage());
        }
    }

    @GetMapping("/following/{userNumber}")
    public ApiResult getFollowings(@PathVariable Long userNumber) {
        try {
            List<Users> followingList = userService.getFollowingList(userNumber);

            List<FollowingsDto> result = followingList.stream()
                    .map(u -> new FollowingsDto(
                            u.getUserNumber(),
                            u.getUserId(),
                            Optional.ofNullable(u.getUserProfile()) // UserProfile 객체가 null 가능성을 Optional로 감싸기
                                    .map(UserProfile::getPicturePath) // UserProfile이 null이 아니면 getPicturePath 호출
                                    .orElse("") // UserProfile이 null이거나 PicturePath가 null이면 빈 문자열 반환
                    )).toList();

            return ApiResult.success(result);
        } catch (Exception e) {
            return ApiResult.error(ResultType.FAIL.getCode(), ResultType.FAIL.getMessage());
        }
    }

    @PostMapping("/user-token")
    public ApiResult addOrUpdateUserToken(@RequestBody UserTokenRequest request) {
        try {
            userService.saveOrUpdateUserToken(request.getUserNumber(), request.getFcmToken());

            return ApiResult.success(null);
        }  catch (UserNotFoundException e) {
            return ApiResult.error(ResultType.NOT_FOUND.getCode(), e.getMessage());
        }
    }

    @GetMapping("/posts/recent/{userNumber}")
    public ApiResult getUserRecentPosts(@PathVariable Long userNumber) {
        try {
            List<Post> myRecentPosts = postService.getMyRecentPosts(userNumber);

            List<UserRecentPostsDto> result = myRecentPosts.stream().map(
                    p -> new UserRecentPostsDto(
                            p.getPostNumber(),
                            p.getPostPicture().getPicturePath()
                    )).toList();

            return ApiResult.success(result);
        }  catch (UserNotFoundException e) {
            return ApiResult.error(ResultType.NOT_FOUND.getCode(), e.getMessage());
        }

    }


}
