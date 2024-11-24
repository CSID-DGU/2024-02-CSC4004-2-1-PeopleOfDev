package com.api.momentup.api;

import com.api.momentup.dto.ApiResult;
import com.api.momentup.dto.ResultType;
import com.api.momentup.dto.user.*;
import com.api.momentup.domain.Users;
import com.api.momentup.exception.UserNotFoundException;
import com.api.momentup.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

        if(userProfile == null || userProfile.isEmpty()) {
            System.out.println("userProfile is Empty");
            userNumber = userService.join(request.getUserId(), request.getUserPw(),
                    request.getUserName(), request.getUserEmail());
        } else {
            try {
                userNumber = userService.join(request.getUserId(), request.getUserPw(),
                        request.getUserName(), request.getUserEmail(), userProfile);
            } catch (Exception e) {
                ApiResult.error(ResultType.FAIL.getCode(), e.getMessage());
            }
        }

        return ApiResult.success(userNumber);
    }

    @GetMapping("/api/v1/user/{id}")
    public ApiResult getUser(@RequestBody @Valid Long userNumber) {

        return ApiResult.success(null);
    }

    @GetMapping("/api/v1/user/login")
    @Operation(summary = "로그인", description = "아이디 패스워드로 로그인")
    public ApiResult login(@RequestBody @Valid LoginRequest request) {
        try {
            Users users = userService.login(request.getUserId(), request.getUserPw());

            System.out.println("user id : "+ users.getUserId());

            return ApiResult.success(new UserDto(users));
        } catch (UserNotFoundException e) {
            return ApiResult.error(ResultType.NOT_FOUND.getCode(), e.getMessage());
        } catch (Exception e) {
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

    @Data
    static class CreateUserResponse {
        private Long id;

        public CreateUserResponse(Long id) {
            this.id = id;
        }
    }
}
