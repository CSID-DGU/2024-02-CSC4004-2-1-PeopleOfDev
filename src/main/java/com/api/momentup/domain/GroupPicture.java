package com.api.momentup.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class GroupPicture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupPictureNumber;

    @OneToOne
    @JoinColumn(name = "group_number")
    private Groups groups;
    private String picturePath;
    private String pictureName;

    public void setGroups(Groups groups) {
        this.groups = groups;
    }

}
