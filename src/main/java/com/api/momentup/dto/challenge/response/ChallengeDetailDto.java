package com.api.momentup.dto.challenge.response;

import com.api.momentup.domain.CalendarResultType;
import com.api.momentup.domain.ChallengeType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ChallengeDetailDto {
    private Long challengeNumber;
    private String groupName;
    private String challengeName;
    private ChallengeType challengeType;
    private String challengePicturePath;
    private List<ChallengeDetailCalendars> calendars;
}


