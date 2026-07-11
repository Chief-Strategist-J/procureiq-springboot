package com.procureiq.springboot_app.features.jobs.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

public class WorkflowRunId implements Serializable {
    private Long id;
    private Instant createdAt;

    public WorkflowRunId() {}

    public WorkflowRunId(Long id, Instant createdAt) {
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
        WorkflowRunId that = (WorkflowRunId) o;
        return Objects.equals(id, that.id) && Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdAt);
    }
}
