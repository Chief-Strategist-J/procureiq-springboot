package com.procureiq.springboot_app.features.fieldservice.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

public class ServiceAppointmentId implements Serializable {
    private Long id;
    private Instant createdAt;

    public ServiceAppointmentId() {}

    public ServiceAppointmentId(Long id, Instant createdAt) {
        this.id = id;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceAppointmentId that = (ServiceAppointmentId) o;
        return Objects.equals(id, that.id) && Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdAt);
    }
}
