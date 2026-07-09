package com.procureiq.springboot_app.features.notifications.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "channel_deliveries")
@IdClass(ChannelDeliveryId.class)
public class ChannelDelivery {

    @Id
    private Long id = 0L;

    @Id
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "notification_id", nullable = false)
    private Long notificationId = 0L;

    @Column(name = "notification_created_at", nullable = false)
    private Instant notificationCreatedAt = Instant.now();

    @Column(name = "user_id", nullable = false)
    private Long userId = 0L;

    @Column(nullable = false)
    private String channel = "";

    @Column(nullable = false)
    private String provider = "";

    @Column(nullable = false)
    private String status = "queued";

    @Column(name = "attempt_count", nullable = false)
    private Short attemptCount = 0;

    @Column(name = "max_attempts", nullable = false)
    private Short maxAttempts = 5;

    @Column(name = "next_retry_at")
    private Instant nextRetryAt;

    @Column(name = "provider_msg_id")
    private String providerMsgId;

    @Column(name = "error_code")
    private String errorCode;

    @Column(name = "error_detail")
    private String errorDetail;

    @Column(name = "sent_at")
    private Instant sentAt;

    @Column(name = "delivered_at")
    private Instant deliveredAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @Column(name = "deleted_at")
    private Instant deletedAt;

    public ChannelDelivery() {}

    public Long getId() {
        return id != null ? id : 0L;
    }

    public void setId(Long id) {
        this.id = id != null ? id : 0L;
    }

    public Instant getCreatedAt() {
        return createdAt != null ? createdAt : Instant.now();
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt != null ? createdAt : Instant.now();
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

    public Long getUserId() {
        return userId != null ? userId : 0L;
    }

    public void setUserId(Long userId) {
        this.userId = userId != null ? userId : 0L;
    }

    public String getChannel() {
        return channel != null ? channel : "";
    }

    public void setChannel(String channel) {
        this.channel = channel != null ? channel : "";
    }

    public String getProvider() {
        return provider != null ? provider : "";
    }

    public void setProvider(String provider) {
        this.provider = provider != null ? provider : "";
    }

    public String getStatus() {
        return status != null ? status : "queued";
    }

    public void setStatus(String status) {
        this.status = status != null ? status : "queued";
    }

    public Short getAttemptCount() {
        return attemptCount != null ? attemptCount : 0;
    }

    public void setAttemptCount(Short attemptCount) {
        this.attemptCount = attemptCount != null ? attemptCount : 0;
    }

    public Short getMaxAttempts() {
        return maxAttempts != null ? maxAttempts : 5;
    }

    public void setMaxAttempts(Short maxAttempts) {
        this.maxAttempts = maxAttempts != null ? maxAttempts : 5;
    }

    public Instant getNextRetryAt() {
        return nextRetryAt;
    }

    public void setNextRetryAt(Instant nextRetryAt) {
        this.nextRetryAt = nextRetryAt;
    }

    public String getProviderMsgId() {
        return providerMsgId;
    }

    public void setProviderMsgId(String providerMsgId) {
        this.providerMsgId = providerMsgId;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDetail() {
        return errorDetail;
    }

    public void setErrorDetail(String errorDetail) {
        this.errorDetail = errorDetail;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    public void setSentAt(Instant sentAt) {
        this.sentAt = sentAt;
    }

    public Instant getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(Instant deliveredAt) {
        this.deliveredAt = deliveredAt;
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
