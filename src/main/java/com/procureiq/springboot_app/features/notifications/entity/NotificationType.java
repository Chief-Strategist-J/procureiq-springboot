package com.procureiq.springboot_app.features.notifications.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "notification_types")
public class NotificationType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id = 0;

    @Column(unique = true, nullable = false)
    private String code = "";

    @Column(nullable = false)
    private String category = "";

    @Column(name = "default_priority", nullable = false)
    private Short defaultPriority = 3;

    @Column(name = "default_channels", columnDefinition = "varchar[]")
    private String[] defaultChannels = new String[0];

    @Column(name = "fan_out_mode", nullable = false)
    private String fanOutMode = "write";

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @Column(name = "deleted_at")
    private Instant deletedAt;

    public NotificationType() {}

    public Short getId() {
        return id != null ? id : 0;
    }

    public void setId(Short id) {
        this.id = id != null ? id : 0;
    }

    public String getCode() {
        return code != null ? code : "";
    }

    public void setCode(String code) {
        this.code = code != null ? code : "";
    }

    public String getCategory() {
        return category != null ? category : "";
    }

    public void setCategory(String category) {
        this.category = category != null ? category : "";
    }

    public Short getDefaultPriority() {
        return defaultPriority != null ? defaultPriority : 3;
    }

    public void setDefaultPriority(Short defaultPriority) {
        this.defaultPriority = defaultPriority != null ? defaultPriority : 3;
    }

    public String[] getDefaultChannels() {
        return defaultChannels != null ? defaultChannels : new String[0];
    }

    public void setDefaultChannels(String[] defaultChannels) {
        this.defaultChannels = defaultChannels != null ? defaultChannels : new String[0];
    }

    public String getFanOutMode() {
        return fanOutMode != null ? fanOutMode : "write";
    }

    public void setFanOutMode(String fanOutMode) {
        this.fanOutMode = fanOutMode != null ? fanOutMode : "write";
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
