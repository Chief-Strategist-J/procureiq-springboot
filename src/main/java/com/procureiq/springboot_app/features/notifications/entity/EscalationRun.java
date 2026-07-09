package com.procureiq.springboot_app.features.notifications.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "escalation_runs")
public class EscalationRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @Column(name = "notification_id", nullable = false)
    private Long notificationId = 0L;

    @Column(name = "notification_created_at", nullable = false)
    private Instant notificationCreatedAt = Instant.now();

    @Column(name = "user_id", nullable = false)
    private Long userId = 0L;

    @Column(name = "policy_id", nullable = false)
    private Long policyId = 0L;

    @Column(name = "current_step", nullable = false)
    private Short currentStep = 0;

    @Column(name = "acknowledged_at")
    private Instant acknowledgedAt;

    @Column(nullable = false)
    private String status = "active";

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @Column(name = "deleted_at")
    private Instant deletedAt;

    public EscalationRun() {}

    public Long getId() {
        return id != null ? id : 0L;
    }

    public void setId(Long id) {
        this.id = id != null ? id : 0L;
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

    public Long getPolicyId() {
        return policyId != null ? policyId : 0L;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId != null ? policyId : 0L;
    }

    public Short getCurrentStep() {
        return currentStep != null ? currentStep : 0;
    }

    public void setCurrentStep(Short currentStep) {
        this.currentStep = currentStep != null ? currentStep : 0;
    }

    public Instant getAcknowledgedAt() {
        return acknowledgedAt;
    }

    public void setAcknowledgedAt(Instant acknowledgedAt) {
        this.acknowledgedAt = acknowledgedAt;
    }

    public String getStatus() {
        return status != null ? status : "active";
    }

    public void setStatus(String status) {
        this.status = status != null ? status : "active";
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
