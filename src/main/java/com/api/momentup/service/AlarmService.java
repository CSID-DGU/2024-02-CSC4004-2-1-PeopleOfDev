package com.api.momentup.service;

import com.api.momentup.domain.*;
import com.api.momentup.domain.Calendar;
import com.api.momentup.exception.*;
import com.api.momentup.repository.*;
import com.api.momentup.utils.AlarmJob;
import com.api.momentup.utils.AlarmUtils;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.api.momentup.utils.AlarmUtils.isNotificationWithinChallengePeriod;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlarmService {
    private final AlarmJpsRepository alarmJpsRepository;
    private final UserJpaRepository userJpaRepository;
    private final Scheduler scheduler;
    private final UserNotificationJpaRepository userNotificationJpaRepository;
    private final FcmService fcmService;
    private final UserTokenJpaRepository userTokenJpaRepository;
    private final GroupJpaRepository groupJpaRepository;
    private final NotificationScheduler notificationScheduler;
    private final UserGroupJpaRepository userGroupJpaRepository;
    private final CalendarService calendarService;

    public void scheduleDailyAlarm(Alarm alarm) throws SchedulerException {
        LocalTime nowTime = LocalTime.now();
        LocalTime startTime = alarm.getStartTime();
        LocalTime endTime = alarm.getEndTime();

        LocalDateTime randomDateTime;

        if (nowTime.isAfter(endTime)) {
            // 현재 시간이 endTime 이후인 경우: 내일의 startTime ~ endTime 범위 사용
            LocalDate tomorrow = LocalDate.now().plusDays(1);
            randomDateTime = AlarmUtils.generateRandomTime(startTime, endTime).withDayOfMonth(tomorrow.getDayOfMonth());
        } else if (nowTime.isBefore(startTime)) {
            // 현재 시간이 startTime 이전인 경우: 오늘의 startTime ~ endTime 범위 사용
            LocalDate today = LocalDate.now();
            randomDateTime = AlarmUtils.generateRandomTime(startTime, endTime).withDayOfMonth(today.getDayOfMonth());
        } else {
            // 현재 시간이 startTime ~ endTime 사이인 경우: 현재 시간 이후 ~ endTime 범위 사용
            randomDateTime = AlarmUtils.generateRandomTime(nowTime, endTime);
        }

        JobKey jobKey = new JobKey("alarm_" + alarm.getAlarmNumber(), "alarmGroup");
        TriggerKey triggerKey = new TriggerKey("trigger_" + alarm.getAlarmNumber(), "alarmGroup");

        // 기존 Job 삭제
        if (scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey);
            System.out.println("Deleted existing job with ID: " + alarm.getAlarmNumber());
        }

        System.out.println("Random DateTime for Alarm: " + randomDateTime);

        JobDetail jobDetail = JobBuilder.newJob(AlarmJob.class)
                .withIdentity("alarm_" + alarm.getAlarmNumber(), "alarmGroup")
                .usingJobData("alarmNumber", alarm.getAlarmNumber())
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger_" + alarm.getAlarmNumber(), "alarmGroup")
                .withSchedule(
                        CronScheduleBuilder.dailyAtHourAndMinute(randomDateTime.getHour(), randomDateTime.getMinute())
                )
                .startNow()
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }

    // 알림 실행
    @Transactional
    public void executeAlarm(Long alarmNumber) {
        // Alarm 조회
        Alarm alarm = alarmJpsRepository.findById(alarmNumber)
                .orElseThrow(() -> new IllegalArgumentException("Alarm not found for ID: " + alarmNumber));

        if(alarm.getStatus().equals(AlarmStatus.DEACTIVE))
            return;

        Users user = alarm.getUsers();

        // Lazy 로딩 필드 강제 초기화
        List<Calendar> calendars = user.getCalendars();
        calendars.size(); // 초기화 트리거

        // 사용자 토큰 조회
        List<UserToken> userTokens = userTokenJpaRepository.findByUsers(user);

        if (userTokens.isEmpty()) {
            throw new IllegalStateException("No FCM tokens found for user ID: " + user.getUserId());
        }

        // UserNotification 생성 및 저장
        UserNotification notification = createAndSaveNotification(alarm, user);

        // FCM 메시지 전송
        sendNotificationsToTokens(notification, userTokens);

        System.out.println("Alarm notification sent and saved for user: " + user.getUserId());

        // 랜덤 시간 생성 및 트리거 재등록
        rescheduleRandomTrigger(alarmNumber, alarm.getStartTime(), alarm.getEndTime());

        notificationScheduler.scheduleTask(notification.getNotificationNumber(), () -> {
            System.out.println("Executing delayed task for notification ID: " + notification.getNotificationNumber());
            calendarService.inputFailResult(user.getUserNumber(), alarm, notification);
        });
    }


    private UserNotification createAndSaveNotification(Alarm alarm, Users user) {
        UserNotification notification = UserNotification.createUserNotification(
                NotificationType.POST,
                alarm.getAlarmNumber(),
                user.getUserId() + "님,",
                "지금 바로 당신의 순간을 업로드 하세요!",
                user,
                alarm.getGroup()
        );

        return userNotificationJpaRepository.save(notification);
    }

    // 알람 저장
    @Transactional
    public Long saveAlarm(Long userNumber, Long groupNumber, String startTime, String endTime) throws UserNotFoundException, SchedulerException, GroupNotFoundException, GroupNotJoinException, UserTokenNotFoundException {
        LocalTime startTimeParse = LocalTime.parse(startTime);
        LocalTime endTimeParse = LocalTime.parse(endTime);

        Users findUser = userJpaRepository.findById(userNumber)
                .orElseThrow(UserNotFoundException::new);

        // FCM 토큰 불러오기
        List<UserToken> findTokens = userTokenJpaRepository.findByUsers(findUser);

        if(findTokens.isEmpty()) {
            throw new UserTokenNotFoundException();
        }


        // 그룹 처리
        Groups group = null;
        if (groupNumber != null) {
            group = groupJpaRepository.findById(groupNumber)
                    .orElseThrow(GroupNotFoundException::new);

            if(!Objects.equals(group.getCreator().getUserNumber(), userNumber)) {
                UserGroups findUserGroups = userGroupJpaRepository.findByGroupsAndUsers(group, findUser)
                        .orElseThrow(GroupNotJoinException::new);
            }
        }

        Alarm alarm = Alarm.createAlarm(findUser, group, startTimeParse, endTimeParse);
        alarmJpsRepository.save(alarm);

        scheduleDailyAlarm(alarm);

        return alarm.getAlarmNumber();
    }

    private void sendNotificationsToTokens(UserNotification notification, List<UserToken> userTokens) {
        for (UserToken userToken : userTokens) {
            try {
                fcmService.sendMessageToToken(
                        notification.getTitle(),
                        notification.getContent(),
                        userToken.getFcmToken(),
                        notification.getNotificationType(),
                        notification.getTargetNumber()
                );
                System.out.println("Successfully sent notification to token: " + userToken.getFcmToken());
            } catch (Exception e) {
                System.err.println("Failed to send FCM message to token: " + userToken.getFcmToken());
                e.printStackTrace();
            }
        }
    }

    @Transactional
    public void removeAlarm(Long alarmNumber) throws SchedulerException, AlarmNotFoundException {
        JobKey jobKey = new JobKey("alarm_" + alarmNumber, "alarmGroup");
        if (scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey);
            System.out.println("Alarm " + alarmNumber + " canceled.");

            Alarm findAlarm = alarmJpsRepository.findById(alarmNumber)
                    .orElseThrow(AlarmNotFoundException::new);

            alarmJpsRepository.delete(findAlarm);
        } else {
            System.out.println("No alarm found with ID: " + alarmNumber);

            throw new AlarmNotFoundException();
        }
    }


    @Transactional
    public void editAlarm(Long alarmNumber, String startTime, String endTime) throws SchedulerException, AlarmNotFoundException {
        // JobKey와 TriggerKey 생성
        TriggerKey triggerKey = new TriggerKey("trigger_" + alarmNumber, "alarmGroup");

        // Job 존재 여부 확인
        if (scheduler.checkExists(triggerKey)) {
            System.out.println("Updating alarm with ID: " + alarmNumber);

            // Alarm 조회
            Alarm findAlarm = alarmJpsRepository.findById(alarmNumber)
                    .orElseThrow(AlarmNotFoundException::new);

            // 새로운 StartTime과 EndTime 설정
            LocalTime startTimeParse = LocalTime.parse(startTime);
            LocalTime endTimeParse = LocalTime.parse(endTime);
            findAlarm.setStartTime(startTimeParse);
            findAlarm.setEndTime(endTimeParse);

            // 새로운 랜덤 시간 생성
            LocalDateTime randomDateTime = AlarmUtils.generateRandomTime(startTimeParse, endTimeParse);

            // 기존 트리거를 새로운 트리거로 교체
            Trigger newTrigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .withSchedule(
                            CronScheduleBuilder.dailyAtHourAndMinute(randomDateTime.getHour(), randomDateTime.getMinute())
                    )
                    .build();

            scheduler.rescheduleJob(triggerKey, newTrigger);

            System.out.println("Alarm rescheduled with new random time: " + randomDateTime);
        } else {
            System.out.println("No alarm found with ID: " + alarmNumber);
            throw new AlarmNotFoundException();
        }
    }

    private void rescheduleRandomTrigger(Long alarmNumber, LocalTime startTime, LocalTime endTime) {
        try {
            // 랜덤 시간 생성
            LocalDateTime randomDateTime = AlarmUtils.generateRandomTime(startTime, endTime);

            // 항상 다음날로 설정
            LocalDate tomorrow = LocalDate.now().plusDays(1);
            randomDateTime = randomDateTime.withYear(tomorrow.getYear())
                    .withMonth(tomorrow.getMonthValue())
                    .withDayOfMonth(tomorrow.getDayOfMonth());

            // 새로운 트리거 생성
            Trigger newTrigger = TriggerBuilder.newTrigger()
                    .withIdentity("trigger_" + alarmNumber, "alarmGroup")
                    .withSchedule(
                            CronScheduleBuilder.dailyAtHourAndMinute(randomDateTime.getHour(), randomDateTime.getMinute())
                    )
                    .startAt(java.sql.Timestamp.valueOf(randomDateTime)) // 정확한 실행 시간 지정
                    .build();

            // 기존 트리거 교체
            TriggerKey triggerKey = new TriggerKey("trigger_" + alarmNumber, "alarmGroup");
            scheduler.rescheduleJob(triggerKey, newTrigger);

            System.out.println("  Next fire time (Quartz): " + newTrigger.getNextFireTime());
            System.out.println("  Expected fire time (Manual): " + randomDateTime);
        } catch (SchedulerException e) {
            throw new IllegalStateException("Failed to reschedule alarm", e);
        }
    }

    @Transactional
    public void changeAlarmState(Long alarmNumber) throws AlarmNotFoundException {
        Alarm findAlarm = alarmJpsRepository.findById(alarmNumber)
                .orElseThrow(AlarmNotFoundException::new);

        if(findAlarm.getStatus().equals(AlarmStatus.ACTIVE)) {
            findAlarm.setActive(AlarmStatus.DEACTIVE);
        } else {
            findAlarm.setActive(AlarmStatus.ACTIVE);
        }
    }

    @Transactional
    public void activeAlarm(Long alarmNumber) throws AlarmNotFoundException {
        Alarm findAlarm = alarmJpsRepository.findById(alarmNumber)
                .orElseThrow(AlarmNotFoundException::new);

        findAlarm.setActive(AlarmStatus.ACTIVE);
    }

    public List<Alarm> getAlarms(Long userNumber) throws UserNotFoundException {
        Users findUser = userJpaRepository.findById(userNumber)
                .orElseThrow(UserNotFoundException::new);

        List<Alarm> findAlarms = alarmJpsRepository.findByUsers(findUser);

        return findAlarms;
    }
}
