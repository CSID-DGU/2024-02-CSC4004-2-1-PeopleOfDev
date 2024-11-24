package com.api.momentup.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
public class Groups {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupNumber;
    private String groupName;
    private String groupTag;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String groupIntro;

    @OneToMany(mappedBy = "groups", cascade = CascadeType.REMOVE)
    private List<UserGroups> userGroups;

    private String groupInviteCode;
    private LocalDateTime createDate;

    // 양방향 OneToOne 매핑
    @OneToOne(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
    private GroupPicture groupPicture;

    public void setGroupPicture(GroupPicture groupPicture) {
        this.groupPicture = groupPicture;
        groupPicture.setGroups(this);
    }

    public static Groups createGroup(String groupName, String groupTag, String groupIntro, GroupPicture groupPicture) {
        Groups groups = new Groups();
        groups.groupName = groupName;
        groups.groupTag = groupTag;
        groups.groupIntro = groupIntro;
        groups.createDate = LocalDateTime.now();
        groups.groupInviteCode = UUID.randomUUID().toString();
        groups.setGroupPicture(groupPicture);

        return groups;
    }

    public static Groups createGroup(String groupName, String groupTag, String groupIntro) {
        Groups groups = new Groups();
        groups.groupName = groupName;
        groups.groupTag = groupTag;
        groups.groupIntro = groupIntro;
        groups.createDate = LocalDateTime.now();
        groups.groupInviteCode = UUID.randomUUID().toString();

        return groups;
    }
}
