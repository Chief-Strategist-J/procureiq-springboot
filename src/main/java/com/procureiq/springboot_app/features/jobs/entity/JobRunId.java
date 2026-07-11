package com.procureiq.springboot_app.features.jobs.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

public class JobRunId implements Serializable {
    private Long id;
    private Instant createdAt;

    public JobRunId() {}

    public JobRunId(Long id, Instant createdAt) {
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
        JobRunId jobRunId = (JobRunId) o;
        return Objects.equals(id, jobRunId.id) && Objects.equals(createdAt, jobRunId.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdAt);
    }
}
