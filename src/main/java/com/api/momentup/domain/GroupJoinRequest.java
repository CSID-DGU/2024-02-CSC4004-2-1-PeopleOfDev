package com.api.momentup.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class GroupJoinRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestNumber;

    @ManyToOne
    private Users users;

    @ManyToOne
    private Groups groups;

    private LocalDateTime requestDate;


    public void setUsers(Users users) {
        this.users = users;
    }

    public void setGroups(Groups groups) {
        this.groups = groups;
    }

    public static GroupJoinRequest createGroupJoinRequest(Users users, Groups groups) {
        GroupJoinRequest groupJoinRequest = new GroupJoinRequest();
        groupJoinRequest.setUsers(users);
        groupJoinRequest.setGroups(groups);
        groupJoinRequest.requestDate = LocalDateTime.now();

        return groupJoinRequest;
    }

}
