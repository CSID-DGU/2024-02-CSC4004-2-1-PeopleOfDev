package com.api.momentup.dto.group;

import lombok.Data;

@Data
public class JoinGroupRequest {
    private Long userNumber;
    private Long groupNumber;
}
