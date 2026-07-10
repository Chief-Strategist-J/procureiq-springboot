package com.procureiq.springboot_app.features.fieldservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "status_transitions")
@IdClass(StatusTransitionId.class)
public class StatusTransition {
    @Id
    @Column(name = "entity_type")
    private String entityType;

    @Id
    @Column(name = "from_status")
    private String fromStatus;

    @Id
    @Column(name = "to_status")
    private String toStatus;

    public StatusTransition() {}

    public StatusTransition(String entityType, String fromStatus, String toStatus) {
        this.entityType = entityType;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
    }

    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }

    public String getFromStatus() { return fromStatus; }
    public void setFromStatus(String fromStatus) { this.fromStatus = fromStatus; }

    public String getToStatus() { return toStatus; }
    public void setToStatus(String toStatus) { this.toStatus = toStatus; }
}
