package com.api.momentup.dto.user.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CreateUserRequest {
    private String userId;
    private String userName;
    private String userEmail;
    private String userPw;
}
