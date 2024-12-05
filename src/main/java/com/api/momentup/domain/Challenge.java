package com.api.momentup.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Challenge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long challengeNumber;

    private String challengeName;

    @ManyToOne
    @JoinColumn(name = "user_number", nullable = false)
    private Users users;

    @ManyToOne
    @JoinColumn(name = "group_number", nullable = true) // 그룹과의 관계 (nullable)
    private Groups groups;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    private ChallengeType challengeType;

    @OneToOne(mappedBy = "challenge", cascade = CascadeType.ALL)
    private ChallengePicture challengePicture;

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChallengeDetail> challengeDetails = new ArrayList<>();

    public void setChallengePicture(ChallengePicture challengePicture) {
        this.challengePicture = challengePicture;
        challengePicture.setChallenge(this);
    }

    // 양방향 관계를 동기화하는 메서드
    public void addChallengeDetail(ChallengeDetail challengeDetail) {
        this.challengeDetails.add(challengeDetail);
        challengeDetail.setChallenge(this);
    }

    public static Challenge createChallenge(String challengeName, Users user, Groups groups,  ChallengeType challengeType) {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusDays(challengeType.getDurationInDays());

        Challenge challenge = new Challenge();
        challenge.challengeName = challengeName;
        challenge.users = user;
        challenge.groups = groups;
        challenge.startDate = startDate;
        challenge.endDate = endDate;
        challenge.challengeType = challengeType;

        return challenge;
    }
}
