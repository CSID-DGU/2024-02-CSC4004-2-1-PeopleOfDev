package com.pplofdev.backend;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Entity
public class GroupManage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ColumnDefault("'active'")
    @Lob
    @Column(name = "status", nullable = false, length = 50, columnDefinition = "VARCHAR(20) DEFAULT 'waiting'")
    private String status;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "joined_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Instant joinedAt;

    public void prePersist() {
        if(status == null) {
            status = "active";
        }
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Instant joinedAt) {
        this.joinedAt = joinedAt;
    }

}