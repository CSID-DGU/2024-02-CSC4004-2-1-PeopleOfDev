package com.api.momentup.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalTime;

@Entity
@Getter
public class Alarm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long alarmNumber;

    @ManyToOne
    @JoinColumn(name = "user_number")
    private Users users;

    @ManyToOne
    @JoinColumn(name = "group_number", nullable = true)
    private Groups group;

    private LocalTime startTime;

    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    private AlarmStatus status;

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public void setActive(AlarmStatus status) {
        this.status = status;
    }

    public static Alarm createAlarm(Users user,Groups group, LocalTime startTime, LocalTime endTime) {
        Alarm alarm = new Alarm();
        alarm.users = user;
        alarm.group = group;
        alarm.startTime = startTime;
        alarm.endTime = endTime;
        alarm.status = AlarmStatus.ACTIVE;

        return alarm;
    }
}
