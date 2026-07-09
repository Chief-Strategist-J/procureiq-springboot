package com.procureiq.springboot_app.features.notifications.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "user_read_cursors")
public class UserReadCursor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @Column(name = "user_id", unique = true, nullable = false)
    private Long userId = 0L;

    @Column(name = "last_read_seq", nullable = false)
    private Long lastReadSeq = 0L;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    public UserReadCursor() {}

    public Long getId() {
        return id != null ? id : 0L;
    }

    public void setId(Long id) {
        this.id = id != null ? id : 0L;
    }

    public Long getUserId() {
        return userId != null ? userId : 0L;
    }

    public void setUserId(Long userId) {
        this.userId = userId != null ? userId : 0L;
    }

    public Long getLastReadSeq() {
        return lastReadSeq != null ? lastReadSeq : 0L;
    }

    public void setLastReadSeq(Long lastReadSeq) {
        this.lastReadSeq = lastReadSeq != null ? lastReadSeq : 0L;
    }

    public Instant getCreatedAt() {
        return createdAt != null ? createdAt : Instant.now();
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt != null ? createdAt : Instant.now();
    }

    public Instant getUpdatedAt() {
        return updatedAt != null ? updatedAt : Instant.now();
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt != null ? updatedAt : Instant.now();
    }
}
