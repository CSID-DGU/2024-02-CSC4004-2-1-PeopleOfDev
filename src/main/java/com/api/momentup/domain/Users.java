package com.api.momentup.domain;


import jakarta.persistence.*;
import lombok.Getter;

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
    @OneToOne(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserProfile userProfile;

    // User가 속한 그룹들
    @OneToMany(mappedBy = "users", cascade = CascadeType.REMOVE)
    private List<UserGroups> userGroups;

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

}
