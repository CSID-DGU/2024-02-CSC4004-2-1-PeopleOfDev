package com.api.momentup.dto.group.request;

import lombok.Data;

@Data
public class CreateGroupRequest {
    private String groupName;
    private String hashTag;
    private String groupIntro;
    private Long userNumber;
}
