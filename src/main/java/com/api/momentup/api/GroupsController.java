package com.api.momentup.api;

import com.api.momentup.domain.Users;
import com.api.momentup.dto.ApiResult;
import com.api.momentup.dto.ResultType;
import com.api.momentup.dto.group.request.CreateGroupRequest;
import com.api.momentup.dto.group.request.InviteCodeJoinGroupRequest;
import com.api.momentup.dto.group.request.JoinGroupRequest;
import com.api.momentup.dto.group.response.GroupJoinUsersDto;
import com.api.momentup.exception.GroupNotFoundException;
import com.api.momentup.exception.UserNotFoundException;
import com.api.momentup.service.GroupService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Tag(name = "Groups API", description = "그룹 관련 API")
public class GroupsController {
    private final GroupService groupService;

    @PostMapping("/api/v1/group/join")
    public ApiResult joinGroup(@RequestBody @Valid JoinGroupRequest request) {
        Long groupNumber = null;

        try {
            groupNumber = groupService.join(request.getGroupNumber(), request.getUserNumber());
        } catch (GroupNotFoundException e) {
            return ApiResult.error(ResultType.NOT_FOUND.getCode(), e.getMessage());
        } catch (UserNotFoundException e) {
            return ApiResult.error(ResultType.NOT_FOUND.getCode(), e.getMessage());
        }

        return ApiResult.success(groupNumber);
    }

    @PostMapping("/api/v1/group/join/{inviteCode}")
    public ApiResult requestGroupJoin(@PathVariable String inviteCode, @RequestBody @Valid InviteCodeJoinGroupRequest request) {
        try {
            Long groupRequestNumber = groupService.requestInviteCodeGroupJoin(request.getUserNumber(), inviteCode);

            return ApiResult.success(groupRequestNumber);
        } catch (UserNotFoundException e) {

            return ApiResult.error(ResultType.NOT_FOUND.getCode(), e.getMessage());
        } catch (GroupNotFoundException e) {
            return ApiResult.error(ResultType.NOT_FOUND.getCode(), e.getMessage());
        }
    }

    @PostMapping("/api/v1/group")
    public ApiResult createGroup(@RequestPart("group") CreateGroupRequest request, @RequestPart(value = "profile", required = false) MultipartFile groupPicture) {
        Long groupNumber = -1L;

        if(groupPicture == null) {
            groupNumber = groupService.createGroup(request.getGroupName(), request.getHashTag(), request.getGroupIntro());
        } else  {
            try {
                groupNumber = groupService.createGroup(request.getGroupName(), request.getHashTag(), request.getGroupIntro(), groupPicture);
            } catch (Exception e) {
                return ApiResult.error(ResultType.NOT_FOUND.getCode(), e.getMessage());
            }
        }

        return ApiResult.success(groupNumber);
    }

    @GetMapping("/api/v1/group/users/{groupNumber}")
    public ApiResult getGroupJoinMember(@PathVariable Long groupNumber) {
        List<Users> groupJoinUsers = groupService.getGroupJoinUsers(groupNumber);

        List<GroupJoinUsersDto> collect = groupJoinUsers.stream()
                .map(u ->
                        new GroupJoinUsersDto(
                                u.getUserNumber(),
                                u.getUserId(),
                                u.getUserProfile() != null && u.getUserProfile().getPicturePath() != null
                                        ? u.getUserProfile().getPicturePath()
                                        : ""
                        )
                )
                .collect(Collectors.toList());

        return ApiResult.success(collect);
    }

}
