package com.api.momentup.api;

import com.api.momentup.domain.Groups;
import com.api.momentup.domain.Post;
import com.api.momentup.domain.Users;
import com.api.momentup.dto.ApiResult;
import com.api.momentup.dto.ResultType;
import com.api.momentup.dto.group.request.CreateGroupRequest;
import com.api.momentup.dto.group.request.InviteCodeJoinGroupRequest;
import com.api.momentup.dto.group.request.JoinGroupRequest;
import com.api.momentup.dto.group.response.GroupInviteCodeDto;
import com.api.momentup.dto.group.response.GroupJoinUsersDto;
import com.api.momentup.dto.group.response.GroupPopularPostsDto;
import com.api.momentup.dto.group.response.GroupRecentPostsDto;
import com.api.momentup.exception.*;
import com.api.momentup.service.GroupService;
import com.api.momentup.service.PostService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/group")
@RequiredArgsConstructor
@Tag(name = "Groups API", description = "그룹 관련 API")
public class GroupsController  {
    private final GroupService groupService;
    private final PostService postService;

    @GetMapping("invite-code/{groupNumber}")
    public ApiResult getGroup(@PathVariable Long groupNumber) {
        try {
            Groups group = groupService.getGroup(groupNumber);

            GroupInviteCodeDto result = new GroupInviteCodeDto(
                    group.getGroupNumber(),
                    group.getGroupName(),
                    group.getGroupPicture() != null ? group.getGroupPicture().getPicturePath() : "",
                    group.getGroupInviteCode()
            );

            return ApiResult.success(result);
        } catch (GroupNotFoundException e) {
            return ApiResult.error(ResultType.NOT_FOUND.getCode(), e.getMessage());
        }

    }

    @PostMapping("/join")
    public ApiResult joinGroup(@RequestBody @Valid JoinGroupRequest request) {
        Long groupNumber = null;

        try {
            groupNumber = groupService.join(request.getGroupNumber(), request.getUserNumber());
        } catch (GroupNotFoundException e) {
            return ApiResult.error(ResultType.NOT_FOUND.getCode(), e.getMessage());
        } catch (UserNotFoundException e) {
            return ApiResult.error(ResultType.NOT_FOUND.getCode(), e.getMessage());
        } catch (NotGroupJoinRequestException | AleadyGroupJoinException e) {
            return ApiResult.error(ResultType.FAIL.getCode(), e.getMessage());
        }

        return ApiResult.success(groupNumber);
    }

    @PostMapping("/join-request/{inviteCode}")
    public ApiResult requestGroupJoin(@PathVariable String inviteCode, @RequestBody @Valid InviteCodeJoinGroupRequest request) {
        try {
            Long groupRequestNumber = groupService.requestInviteCodeGroupJoin(request.getUserNumber(), inviteCode);

            return ApiResult.success(groupRequestNumber);
        } catch (UserNotFoundException e) {
            return ApiResult.error(ResultType.NOT_FOUND.getCode(), e.getMessage());
        } catch (GroupNotFoundException e) {
            return ApiResult.error(ResultType.NOT_FOUND.getCode(), e.getMessage());
        } catch (AlreadyGroupJoinRequestException e) {
            return ApiResult.error(ResultType.FAIL.getCode(), e.getMessage());
        }
    }

    @PostMapping()
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

    @GetMapping("/users/{groupNumber}")
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

    @GetMapping("/posts/popular/{groupNumber}")
    public ApiResult getGroupPopularPosts(@PathVariable Long groupNumber) {
        try {
            List<Post> groupPopularPosts = postService.getPopularPostsByGroup(groupNumber);

            List<GroupPopularPostsDto> collect = groupPopularPosts.stream()
                    .map(p -> new GroupPopularPostsDto(
                        p.getPostNumber(),
                        p.getContent(),
                        p.getWriter().getUserId(),
                        p.getPostPicture().getPicturePath()
                    ))
                    .toList();

            return ApiResult.success(collect);
        } catch (Exception e) {
            return ApiResult.error(ResultType.FAIL.getCode(), ResultType.FAIL.getMessage());
        }
    }

    // 그룹별 최신 게시물
    @GetMapping("/posts/recent/{groupNumber}")
    public ApiResult getGroupRecentPosts(@PathVariable Long groupNumber) {
        try {
            List<Post> latestPostsByGroup = postService.getLatestPostsByGroup(groupNumber);

            List<GroupRecentPostsDto> result = latestPostsByGroup
                    .stream()
                    .map(p -> new GroupRecentPostsDto(
                            p.getPostNumber(),
                            p.getWriter().getUserId(),
                            p.getPostPicture().getPicturePath()
                    )).toList();

            return ApiResult.success(result);
        } catch (Exception e) {
            return ApiResult.error(ResultType.FAIL.getCode(), e.getMessage());
        }
    }

    @DeleteMapping("/join-request/{requestNumber}")
    public ApiResult rejectGroupJoinRequest(@PathVariable Long groupJoinRequestNumber) {
        try {
            groupService.rejectGroupJoinRequest(groupJoinRequestNumber);

            return ApiResult.success(null);
        } catch (NotGroupJoinRequestException e) {
            return ApiResult.error(ResultType.NOT_FOUND.getCode(), e.getMessage());
        } catch (Exception e) {
            return ApiResult.error(ResultType.FAIL.getCode(), ResultType.FAIL.getMessage());
        }
    }

    @DeleteMapping("/{groupNumber}/users/{userNumber}")
    public ApiResult expelGroupUser(@PathVariable Long groupNumber, @PathVariable Long userNumber) {
        try {
            groupService.expelGroupUser(groupNumber, userNumber);

            return ApiResult.success(null);
        } catch (GroupNotFoundException | UserNotFoundException | GroupNotJoinException e) {
            return ApiResult.error(ResultType.NOT_FOUND.getCode(), e.getMessage());
        }
    }

}
