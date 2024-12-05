package com.api.momentup.dto.alarm.request;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AlarmSaveRequest {
    private Long userNumber;
    private Long groupNumber;
    @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "Invalid time format, expected HH:mm")
    private String startTime;
    @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "Invalid time format, expected HH:mm")
    private String endTime;
}
