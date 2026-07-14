package com.procureiq.springboot_app.features.jobs.dto.response;

import java.time.Instant;

public record WorkflowRunResponse(
    Long id,
    Long workflowId,
    String status,
    Instant startedAt,
    Instant completedAt,
    Instant createdAt
) {
    public static WorkflowRunResponse fromEntity(final com.procureiq.springboot_app.features.jobs.entity.WorkflowRun entity) {
        return new WorkflowRunResponse(
            entity.getId(),
                    entity.getWorkflowId(),
                    entity.getStatus(),
                    entity.getStartedAt(),
                    entity.getCompletedAt(),
                    entity.getCreatedAt()
        );
    }
}
