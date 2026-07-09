package com.procureiq.springboot_app.features.notifications.entity;

import com.procureiq.springboot_app.shared.utils.JsonMapConverter;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "notification_templates")
public class NotificationTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private String channel = "";

    @Column(nullable = false)
    private String locale = "en";

    @Column(nullable = false)
    private Integer version = 1;

    @Column(name = "subject_tmpl", nullable = false)
    private String subjectTmpl = "";

    @Column(name = "body_tmpl", nullable = false)
    private String bodyTmpl = "";

    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    @Column(name = "variables_schema", nullable = false)
    private Map<String, Object> variablesSchema = new HashMap<>();

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @Column(name = "deleted_at")
    private Instant deletedAt;

    public NotificationTemplate() {}

    public Long getId() {
        return id != null ? id : 0L;
    }

    public void setId(Long id) {
        this.id = id != null ? id : 0L;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getChannel() {
        return channel != null ? channel : "";
    }

    public void setChannel(String channel) {
        this.channel = channel != null ? channel : "";
    }

    public String getLocale() {
        return locale != null ? locale : "en";
    }

    public void setLocale(String locale) {
        this.locale = locale != null ? locale : "en";
    }

    public Integer getVersion() {
        return version != null ? version : 1;
    }

    public void setVersion(Integer version) {
        this.version = version != null ? version : 1;
    }

    public String getSubjectTmpl() {
        return subjectTmpl != null ? subjectTmpl : "";
    }

    public void setSubjectTmpl(String subjectTmpl) {
        this.subjectTmpl = subjectTmpl != null ? subjectTmpl : "";
    }

    public String getBodyTmpl() {
        return bodyTmpl != null ? bodyTmpl : "";
    }

    public void setBodyTmpl(String bodyTmpl) {
        this.bodyTmpl = bodyTmpl != null ? bodyTmpl : "";
    }

    public Map<String, Object> getVariablesSchema() {
        return variablesSchema != null ? variablesSchema : new HashMap<>();
    }

    public void setVariablesSchema(Map<String, Object> variablesSchema) {
        this.variablesSchema = variablesSchema != null ? variablesSchema : new HashMap<>();
    }

    public Boolean getIsActive() {
        return isActive != null ? isActive : true;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive != null ? isActive : true;
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
