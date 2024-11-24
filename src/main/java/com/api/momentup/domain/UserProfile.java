package com.api.momentup.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userProfileNumber;

    @OneToOne
    @JoinColumn(name = "user_number")
    private Users users;

    private String picturePath;

    private String pictureName;

    public void setUsers(Users users) {
        this.users = users;
    }



    public static UserProfile createUesrProfile(Users users, String picturePath, String pictureName) {
        UserProfile userProfile = new UserProfile();
        userProfile.users = users;
        userProfile.pictureName = pictureName;
        userProfile.picturePath = picturePath;

        return userProfile;
    }

}
