package com.procureiq.springboot_app.features.notifications.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "notification_recipients")
@IdClass(NotificationRecipientId.class)
public class NotificationRecipient {

    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId = 0L;

    @Id
    @Column(name = "notification_id", nullable = false)
    private Long notificationId = 0L;

    @Column(name = "notification_created_at", nullable = false)
    private Instant notificationCreatedAt = Instant.now();

    @Column(nullable = false)
    private String status = "pending";

    @Column(name = "read_at")
    private Instant readAt;

    @Column(name = "dismissed_at")
    private Instant dismissedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @Column(name = "deleted_at")
    private Instant deletedAt;

    public NotificationRecipient() {}

    public Long getUserId() {
        return userId != null ? userId : 0L;
    }

    public void setUserId(Long userId) {
        this.userId = userId != null ? userId : 0L;
    }

    public Long getNotificationId() {
        return notificationId != null ? notificationId : 0L;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId != null ? notificationId : 0L;
    }

    public Instant getNotificationCreatedAt() {
        return notificationCreatedAt != null ? notificationCreatedAt : Instant.now();
    }

    public void setNotificationCreatedAt(Instant notificationCreatedAt) {
        this.notificationCreatedAt = notificationCreatedAt != null ? notificationCreatedAt : Instant.now();
    }

    public String getStatus() {
        return status != null ? status : "pending";
    }

    public void setStatus(String status) {
        this.status = status != null ? status : "pending";
    }

    public Instant getReadAt() {
        return readAt;
    }

    public void setReadAt(Instant readAt) {
        this.readAt = readAt;
    }

    public Instant getDismissedAt() {
        return dismissedAt;
    }

    public void setDismissedAt(Instant dismissedAt) {
        this.dismissedAt = dismissedAt;
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

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }
}
