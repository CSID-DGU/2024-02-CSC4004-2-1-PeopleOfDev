package com.api.momentup.dto.group.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class HomeGroupsDto {
    private Long groupNumber;
    private String groupName;
    private LocalDateTime lastPostTime;
    private String timeAgo; // 시간 정보를 포함하는 필드 추가

    public HomeGroupsDto(Long groupNumber, String groupName, LocalDateTime lastPostTime) {
        this.groupNumber = groupNumber;
        this.groupName = groupName;
        this.lastPostTime = lastPostTime;
        this.timeAgo = getTimeAgo(); // 생성 시 계산
    }

    public String getTimeAgo() {
        if (lastPostTime == null) {
            return "";
        }

        Duration duration = Duration.between(lastPostTime, LocalDateTime.now());
        long seconds = duration.getSeconds();
        long minutes = duration.toMinutes();
        long hours = duration.toHours();
        long days = duration.toDays();
        long weeks = days / 7;
        long years = days / 365; // 윤년은 무시하고 단순 계산

        if (seconds < 60) {
            return seconds + "초 전 업데이트";
        } else if (minutes < 60) {
            return minutes + "분 전 업데이트";
        } else if (hours < 24) {
            return hours + "시간 전 업데이트";
        } else if (days < 7) {
            return days + "일 전 업데이트";
        } else if (weeks < 52) {
            return weeks + "주 전 업데이트";
        } else {
            return years + "년 전 업데이트";
        }
    }
}
