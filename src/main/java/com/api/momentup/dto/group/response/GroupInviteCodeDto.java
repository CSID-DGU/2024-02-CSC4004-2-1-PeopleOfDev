package com.api.momentup.dto.group.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GroupInviteCodeDto {
    private Long groupNumber;
    private String groupName;
    private String groupPicturePath;
    private String inviteCode;
}
