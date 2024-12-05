package com.api.momentup.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

@Entity
@Getter
public class ChallengeDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long challengeDetailNumber;

    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "challenge_number", nullable = false)
    private Challenge challenge;

    @Enumerated(EnumType.STRING)
    private CalendarResultType calendarResult;

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    public static ChallengeDetail createChallengeDetail(CalendarResultType calendarResultType) {
        ChallengeDetail challengeDetail = new ChallengeDetail();
        challengeDetail.date = LocalDate.now();
        challengeDetail.calendarResult = calendarResultType;

        return challengeDetail;
    }
}
