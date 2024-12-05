package com.api.momentup.exception;

public class ChallengeNotFoundException extends Exception{
    public ChallengeNotFoundException() {
        super("조회하려고 하는 챌린지가 존재하지 않습니다.");
    }
}
