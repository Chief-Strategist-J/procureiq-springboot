package com.procureiq.springboot_app.features.notifications.entity;

import com.procureiq.springboot_app.shared.utils.JsonMapConverter;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "routing_rules")
public class RoutingRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private NotificationType type;

    @Column(name = "priority_order", nullable = false)
    private Integer priorityOrder = 100;

    @Convert(converter = JsonMapConverter.class)
    @Column(nullable = false, columnDefinition = "jsonb")
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    private Map<String, Object> condition = new HashMap<>();

    @Column(name = "channels", columnDefinition = "varchar[]")
    private String[] channels = new String[0];

    @Column(name = "escalation_policy_id")
    private Long escalationPolicyId;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @Column(name = "deleted_at")
    private Instant deletedAt;

    public RoutingRule() {}

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

    public Integer getPriorityOrder() {
        return priorityOrder != null ? priorityOrder : 100;
    }

    public void setPriorityOrder(Integer priorityOrder) {
        this.priorityOrder = priorityOrder != null ? priorityOrder : 100;
    }

    public Map<String, Object> getCondition() {
        return condition != null ? condition : new HashMap<>();
    }

    public void setCondition(Map<String, Object> condition) {
        this.condition = condition != null ? condition : new HashMap<>();
    }

    public String[] getChannels() {
        return channels != null ? channels : new String[0];
    }

    public void setChannels(String[] channels) {
        this.channels = channels != null ? channels : new String[0];
    }

    public Long getEscalationPolicyId() {
        return escalationPolicyId;
    }

    public void setEscalationPolicyId(Long escalationPolicyId) {
        this.escalationPolicyId = escalationPolicyId;
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
