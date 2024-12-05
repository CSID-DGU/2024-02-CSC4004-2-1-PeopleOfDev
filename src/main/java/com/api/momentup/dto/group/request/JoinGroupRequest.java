package com.api.momentup.dto.group.request;

import lombok.Data;

@Data
public class JoinGroupRequest {
    private Long userNumber;
    private Long groupNumber;
}
