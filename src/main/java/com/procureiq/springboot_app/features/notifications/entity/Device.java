package com.procureiq.springboot_app.features.notifications.entity;

import com.procureiq.springboot_app.shared.utils.JsonMapConverter;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "devices")
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @Column(name = "user_id", nullable = false)
    private Long userId = 0L;

    @Column(nullable = false)
    private String platform = "";

    @Column(name = "push_token")
    private String pushToken;

    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    @Column(name = "web_push_endpoint")
    private Map<String, Object> webPushEndpoint = new HashMap<>();

    @Column(name = "app_version")
    private String appVersion;

    @Column(name = "os_version")
    private String osVersion;

    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    @Column(nullable = false)
    private Map<String, Object> capabilities = new HashMap<>();

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "last_seen_at", nullable = false)
    private Instant lastSeenAt = Instant.now();

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @Column(name = "deleted_at")
    private Instant deletedAt;

    public Device() {}

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

    public String getPlatform() {
        return platform != null ? platform : "";
    }

    public void setPlatform(String platform) {
        this.platform = platform != null ? platform : "";
    }

    public String getPushToken() {
        return pushToken;
    }

    public void setPushToken(String pushToken) {
        this.pushToken = pushToken;
    }

    public Map<String, Object> getWebPushEndpoint() {
        return webPushEndpoint != null ? webPushEndpoint : new HashMap<>();
    }

    public void setWebPushEndpoint(Map<String, Object> webPushEndpoint) {
        this.webPushEndpoint = webPushEndpoint != null ? webPushEndpoint : new HashMap<>();
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public Map<String, Object> getCapabilities() {
        return capabilities != null ? capabilities : new HashMap<>();
    }

    public void setCapabilities(Map<String, Object> capabilities) {
        this.capabilities = capabilities != null ? capabilities : new HashMap<>();
    }

    public Boolean getIsActive() {
        return isActive != null ? isActive : true;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive != null ? isActive : true;
    }

    public Instant getLastSeenAt() {
        return lastSeenAt != null ? lastSeenAt : Instant.now();
    }

    public void setLastSeenAt(Instant lastSeenAt) {
        this.lastSeenAt = lastSeenAt != null ? lastSeenAt : Instant.now();
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
