package com.api.momentup.dto.user;

import lombok.Data;

@Data
public class FindUserIdRequest {
    private String userName;
    private String userEmail;
}
