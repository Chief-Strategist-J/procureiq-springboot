package com.procureiq.springboot_app.features.jobs.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

@Entity
@Table(name = "workflow_runs")
@IdClass(WorkflowRunId.class)
public class WorkflowRun {
    @Id
    private Long id;

    @Column(name = "workflow_id", nullable = false)
    private Long workflowId;

    @Column(nullable = false)
    private String status = "pending";

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Id
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public WorkflowRun() {}

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);
        }
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getWorkflowId() { return workflowId; }
    public void setWorkflowId(Long workflowId) { this.workflowId = workflowId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getStartedAt() { return startedAt; }
    public void setStartedAt(Instant startedAt) { this.startedAt = startedAt; }
    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
