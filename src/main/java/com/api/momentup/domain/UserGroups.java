package com.api.momentup.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class UserGroups {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userGroupNumber;

    @ManyToOne
    @JoinColumn(name = "user_number", nullable = false)
    private Users users;

    @ManyToOne()
    @JoinColumn(name = "group_number", nullable = false)
    private Groups groups;

    public static UserGroups createUserGroup(Users users, Groups groups) {
        UserGroups userGroups = new UserGroups();
        userGroups.users = users;
        userGroups.groups = groups;

        return userGroups;
    }
}
