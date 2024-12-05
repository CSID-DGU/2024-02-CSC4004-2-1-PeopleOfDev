package com.api.momentup.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class ChallengePicture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long challengePictureNumber;

    @OneToOne
    @JoinColumn(name = "challenge_number")
    private Challenge challenge;

    private String picturePath;
    private String pictureName;

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    public static ChallengePicture createChallengePicture( String picturePath, String pictureName) {
        ChallengePicture challengePicture = new ChallengePicture();
        challengePicture.picturePath = picturePath;
        challengePicture.pictureName = pictureName;

        return challengePicture;
    }
}
