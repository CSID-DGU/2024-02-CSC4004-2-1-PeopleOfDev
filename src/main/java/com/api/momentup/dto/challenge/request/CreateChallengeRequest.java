package com.api.momentup.dto.challenge.request;

import com.api.momentup.domain.ChallengeType;
import lombok.Data;

@Data
public class CreateChallengeRequest {
    private Long userNumber;
    private Long groupNumber;
    private String challengeName;
    private ChallengeType challengeType;
}
