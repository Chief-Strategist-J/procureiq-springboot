package com.procureiq.springboot_app.features.jobs.dto.response;

import java.time.Instant;

public record WorkflowResponse(
    Long id,
    Long orgId,
    String name,
    String status,
    Instant createdAt,
    Instant updatedAt
) {
    public static WorkflowResponse fromEntity(final com.procureiq.springboot_app.features.jobs.entity.Workflow entity) {
        return new WorkflowResponse(
            entity.getId(),
                    entity.getOrganization().getId(),
                    entity.getName(),
                    entity.getStatus(),
                    entity.getCreatedAt(),
                    entity.getUpdatedAt()
        );
    }
}
