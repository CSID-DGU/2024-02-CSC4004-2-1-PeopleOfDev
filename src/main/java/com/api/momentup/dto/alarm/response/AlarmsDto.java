package com.api.momentup.dto.alarm.response;

import com.api.momentup.domain.AlarmStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AlarmsDto {
    private Long alarmNumber;
    private String groupName;
    private String startTime;
    private String endTime;
    private AlarmStatus isActive;
}
