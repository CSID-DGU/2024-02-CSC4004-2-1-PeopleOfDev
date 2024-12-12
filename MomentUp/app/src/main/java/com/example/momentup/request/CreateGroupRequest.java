package com.example.momentup.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateGroupRequest {
    private String groupName;
    private String hashTag;
    private String groupIntro;
    private Long userNumber;
}
