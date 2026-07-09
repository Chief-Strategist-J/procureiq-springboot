package com.procureiq.springboot_app.features.notifications.entity;

import com.procureiq.springboot_app.shared.utils.JsonMapConverter;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "notifications")
@IdClass(NotificationId.class)
public class Notification {

    @Id
    private Long id = 0L;

    @Id
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private NotificationType type;

    @Column(name = "source_service", nullable = false)
    private String sourceService = "";

    @Column(name = "dedup_key")
    private String dedupKey;

    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    @Column(nullable = false)
    private Map<String, Object> payload = new HashMap<>();

    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    @Column(nullable = false)
    private Map<String, Object> metadata = new HashMap<>();

    @Column(nullable = false)
    private Short priority = 3;

    @Column(name = "target_scope", nullable = false)
    private String targetScope = "user";

    @Column(name = "target_id")
    private Long targetId;

    @Column(name = "global_seq", nullable = false)
    private Long globalSeq = 0L;

    @Column(name = "scheduled_for")
    private Instant scheduledFor;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @Column(name = "deleted_at")
    private Instant deletedAt;

    public Notification() {}

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

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getSourceService() {
        return sourceService != null ? sourceService : "";
    }

    public void setSourceService(String sourceService) {
        this.sourceService = sourceService != null ? sourceService : "";
    }

    public String getDedupKey() {
        return dedupKey;
    }

    public void setDedupKey(String dedupKey) {
        this.dedupKey = dedupKey;
    }

    public Map<String, Object> getPayload() {
        return payload != null ? payload : new HashMap<>();
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload != null ? payload : new HashMap<>();
    }

    public Map<String, Object> getMetadata() {
        return metadata != null ? metadata : new HashMap<>();
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata != null ? metadata : new HashMap<>();
    }

    public Short getPriority() {
        return priority != null ? priority : 3;
    }

    public void setPriority(Short priority) {
        this.priority = priority != null ? priority : 3;
    }

    public String getTargetScope() {
        return targetScope != null ? targetScope : "user";
    }

    public void setTargetScope(String targetScope) {
        this.targetScope = targetScope != null ? targetScope : "user";
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public Long getGlobalSeq() {
        return globalSeq != null ? globalSeq : 0L;
    }

    public void setGlobalSeq(Long globalSeq) {
        this.globalSeq = globalSeq != null ? globalSeq : 0L;
    }

    public Instant getScheduledFor() {
        return scheduledFor;
    }

    public void setScheduledFor(Instant scheduledFor) {
        this.scheduledFor = scheduledFor;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
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
