package com.api.momentup.dto.challenge.response;

import com.api.momentup.domain.CalendarResultType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChallengeDetailCalendars {
    private Long challengeDetailNumber;
    private String date;
    private CalendarResultType calendarResultType;
}
