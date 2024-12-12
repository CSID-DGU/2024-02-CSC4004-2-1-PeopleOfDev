package com.api.momentup.api;

import com.api.momentup.domain.Alarm;
import com.api.momentup.domain.Groups;
import com.api.momentup.dto.ApiResult;
import com.api.momentup.dto.ResultType;
import com.api.momentup.dto.alarm.request.AlarmEditRequest;
import com.api.momentup.dto.alarm.request.AlarmSaveRequest;
import com.api.momentup.dto.alarm.response.AlarmsDto;
import com.api.momentup.exception.*;
import com.api.momentup.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/alarm")
@RequiredArgsConstructor
public class AlarmController {
    private final AlarmService alarmService;

    @PostMapping("")
    public ApiResult saveAlarm(@RequestBody AlarmSaveRequest request) {
        try {
            Long saveAlarmNumber = alarmService.saveAlarm(request.getUserNumber(), request.getGroupNumber(), request.getStartTime(), request.getEndTime());

            return ApiResult.success(saveAlarmNumber);
        }  catch (UserNotFoundException | GroupNotFoundException e) {
            return ApiResult.error(ResultType.NOT_FOUND.getCode(), e.getMessage());
        } catch (SchedulerException | GroupNotJoinException e) {
            return ApiResult.error(ResultType.FAIL.getCode(), e.getMessage());
        } catch (UserTokenNotFoundException e) {
            return ApiResult.error(ResultType.FAIL.getCode(), e.getMessage());
        }
    }

    @PutMapping("")
    public ApiResult editAlarm(@RequestBody AlarmEditRequest request) {
        try {
            alarmService.editAlarm(request.getAlarmNumber(), request.getStartTime(), request.getEndTime());

            return ApiResult.success(null);
        }  catch (SchedulerException e) {
            return ApiResult.error(ResultType.FAIL.getCode(), ResultType.FAIL.getMessage());
        } catch (AlarmNotFoundException e) {
            return ApiResult.error(ResultType.NOT_FOUND.getCode(), e.getMessage());
        }
    }

    @DeleteMapping("/{alarmNumber}")
    public ApiResult removeAlarm(@PathVariable Long alarmNumber) {
        try {
            alarmService.removeAlarm(alarmNumber);

            return ApiResult.success(null);
        } catch (SchedulerException e) {
            return ApiResult.error(ResultType.FAIL.getCode(), ResultType.FAIL.getMessage());
        } catch (AlarmNotFoundException e) {
            return ApiResult.error(ResultType.NOT_FOUND.getCode(), e.getMessage());
        }
    }

    @GetMapping("/{userNumber}")
    public ApiResult getAlarms(@PathVariable Long userNumber) {
        try {
            List<Alarm> alarms = alarmService.getAlarms(userNumber);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

            List<AlarmsDto> result = alarms.stream().map(
                    a -> new AlarmsDto(
                            a.getAlarmNumber(),
                            Optional.ofNullable(a.getGroup()) // UserProfile 객체가 null 가능성을 Optional로 감싸기
                                    .map(Groups::getGroupName) // UserProfile이 null이 아니면 getPicturePath 호출
                                    .orElse("전체"),
                            a.getStartTime().format(formatter),
                            a.getEndTime().format(formatter),
                            a.getStatus()
                    )
            ).toList();

            return ApiResult.success(result);
        } catch (UserNotFoundException e) {
            return ApiResult.error(ResultType.NOT_FOUND.getCode(), e.getMessage());
        }
    }

    @PutMapping("/status/{alarmNumber}")
    public ApiResult changeAlarmState(@PathVariable Long alarmNumber) {
        try {
            alarmService.changeAlarmState(alarmNumber);

            return ApiResult.success(null);
        } catch (AlarmNotFoundException e) {
            return ApiResult.error(ResultType.NOT_FOUND.getCode(), e.getMessage());
        }
    }
}
