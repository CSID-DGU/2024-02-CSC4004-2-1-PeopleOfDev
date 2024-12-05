package com.api.momentup.dto.challenge.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChallengesDto {
    private Long challengeNumber;
    private String groupName;
    private String challengePicturePath;
}
