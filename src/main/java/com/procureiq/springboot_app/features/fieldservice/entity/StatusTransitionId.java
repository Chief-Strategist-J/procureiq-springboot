package com.procureiq.springboot_app.features.fieldservice.entity;

import java.io.Serializable;
import java.util.Objects;

public class StatusTransitionId implements Serializable {
    private String entityType;
    private String fromStatus;
    private String toStatus;

    public StatusTransitionId() {}

    public StatusTransitionId(String entityType, String fromStatus, String toStatus) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatusTransitionId that = (StatusTransitionId) o;
        return Objects.equals(entityType, that.entityType) &&
               Objects.equals(fromStatus, that.fromStatus) &&
               Objects.equals(toStatus, that.toStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityType, fromStatus, toStatus);
    }
}
