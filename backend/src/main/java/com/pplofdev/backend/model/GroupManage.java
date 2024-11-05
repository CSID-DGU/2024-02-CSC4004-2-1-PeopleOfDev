package com.pplofdev.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Setter
@Getter
@Entity
@Table(name = "groupmanage", schema = "pplofdev")
public class GroupManage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ColumnDefault("'active'")
    @Lob
    @Column(name = "status", nullable = false, length = 50, columnDefinition = "VARCHAR(50) DEFAULT 'waiting'")
    private String status;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "joined_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Instant joinedAt;

    @PrePersist
    public void prePersist() {
        if(status == null) {
            status = "waiting";
        }
    }

}