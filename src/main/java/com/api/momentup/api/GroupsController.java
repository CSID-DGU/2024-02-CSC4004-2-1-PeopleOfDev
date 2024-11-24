package com.api.momentup.api;

import com.api.momentup.dto.ApiResult;
import com.api.momentup.dto.group.JoinGroupRequest;
import com.api.momentup.service.GroupService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Groups API", description = "그룹 관련 API")
public class GroupsController {
    private final GroupService groupService;

    @PostMapping("/api/v1/group")
    public ApiResult joinGroup(@RequestBody @Valid JoinGroupRequest request) {
        groupService.join(request.getGroupNumber(), request.getUserNumber());

        return ApiResult.success(null);
    }


}
