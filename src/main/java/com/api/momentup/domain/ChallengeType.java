package com.api.momentup.domain;

public enum ChallengeType {
    SEVEN_DAYS(7),
    THIRTY_DAYS(30);

    private final int durationInDays;

    ChallengeType(int durationInDays) {
        this.durationInDays = durationInDays;
    }

    public int getDurationInDays() {
        return durationInDays;
    }
}
