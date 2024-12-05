package com.api.momentup.utils;

import com.api.momentup.service.AlarmService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AlarmJob implements Job {
    @Autowired
    private AlarmService alarmService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Long alarmNumber = context.getJobDetail().getJobDataMap().getLong("alarmNumber");

        System.out.println("Sending alarm notification for alarm: " + alarmNumber);

        // 알림 실행 로직
        alarmService.executeAlarm(alarmNumber);
    }
}
