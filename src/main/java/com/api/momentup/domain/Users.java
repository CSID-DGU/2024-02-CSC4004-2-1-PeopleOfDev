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
    private List<UserGroups> userGroups = new ArrayList<>();;

    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL)
    private List<Follow> following = new ArrayList<>(); // 내가 팔로우한 사람들

    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL)
    private List<Follow> followers = new ArrayList<>(); // 나를 팔로우한 사람들

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Calendar> calendars = new ArrayList<>();;

    public void addCalendar(Calendar calendar) {
        this.calendars.add(calendar); // Users의 리스트에 추가
        calendar.setUser(this);      // Calendar의 Users도 동기화
    }

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
