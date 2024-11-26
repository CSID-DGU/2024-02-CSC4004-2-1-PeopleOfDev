package com.api.momentup.api;

import com.api.momentup.dto.ApiResult;
import com.api.momentup.dto.ResultType;
import com.api.momentup.dto.group.response.HomeGroupsDto;
import com.api.momentup.domain.Users;
import com.api.momentup.dto.user.request.*;
import com.api.momentup.dto.user.response.UserDto;
import com.api.momentup.exception.NotificationNotFoundException;
import com.api.momentup.exception.UserDuplicateException;
import com.api.momentup.exception.UserNotFoundException;
import com.api.momentup.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(produces = "application/json; charset=utf8")
@RequiredArgsConstructor
@Tag(name = "User API", description = "사용자 관련 API")
public class UserApiController {
    private final UserService userService;

    @PostMapping(value = "/api/v1/users", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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

    @GetMapping("/api/v1/user/{userNumber}")
    public ApiResult getUser(@PathVariable Long postNumber, @RequestBody @Valid Long userNumber) {

        return ApiResult.success(null);
    }

    @GetMapping("/api/v1/user/login")
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

    @PostMapping("/api/v1/users/id")
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

    @PostMapping("/api/v1/users/password")
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

    @GetMapping("/api/v1/users/groups/{userNumber}")
    public ApiResult getUserGroups(@PathVariable Long userNumber) {
        List<HomeGroupsDto> findGroups = userService.getUserGroups(userNumber);

        if(findGroups.isEmpty()) {
            return ApiResult.error(ResultType.NOT_FOUND.getCode(), "게시물 없음");
        }

        return ApiResult.success(findGroups);
    }

    @PostMapping("/api/v1/users/follow/request")
    public ApiResult requestFollow(@RequestBody FollowRequest request) {
        try {
            Long notificationNumber = userService.requestFollow(request.getOwnUserNumber(), request.getTargetUserNumber());

            return ApiResult.success(notificationNumber);
        } catch (UserNotFoundException e) {
            return ApiResult.error(ResultType.NOT_FOUND.getCode(), e.getMessage());
        }
    }

    @PostMapping("/api/v1/users/follow")
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
        }
    }
}
