package com.api.momentup.dto.group.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GroupJoinUsersDto {
    private Long userNumber;
    private String userId;
    private String userProfile;
}
