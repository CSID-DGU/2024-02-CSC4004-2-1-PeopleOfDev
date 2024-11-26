package com.api.momentup.domain;


import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Users {
    @Id
    @Column(name = "user_number")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userNumber;
    private String userName;
    private String userId;
    private String userPw;
    private String userEmail;

    // 양방향 OneToOne 매핑
    @OneToOne(mappedBy = "users", cascade = CascadeType.ALL)
    private UserProfile userProfile;

    // User가 속한 그룹들
    @OneToMany(mappedBy = "users", cascade = CascadeType.REMOVE)
    private List<UserGroups> userGroups;

    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL)
    private List<Follow> following = new ArrayList<>(); // 내가 팔로우한 사람들

    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL)
    private List<Follow> followers = new ArrayList<>(); // 나를 팔로우한 사람들

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
        userProfile.setUsers(this); // 관계 설정을 위한 편의 메서드
    }


    public static Users createMember(
            String userId, String userName, String userPw, String userEmail
    ) {
        Users users = new Users();
        users.userId = userId;
        users.userName = userName;
        users.userPw = userPw;
        users.userEmail = userEmail;

        return users;
    }

    public static Users createMember(
            String userId, String userName, String userPw, String userEmail, UserProfile userProfile
    ) {
        Users users = new Users();
        users.userId = userId;
        users.userName = userName;
        users.userPw = userPw;
        users.userEmail = userEmail;
        users.setUserProfile(userProfile);

        return users;
    }

}
