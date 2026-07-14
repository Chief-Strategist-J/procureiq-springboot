package com.procureiq.springboot_app.features.jobs.dto.response;

import java.time.Instant;
import java.util.Map;

public record JobResponse(
    Long id,
    Long orgId,
    Long categoryId,
    String name,
    String status,
    Map<String, Object> config,
    Instant createdAt,
    Instant updatedAt
) {
    public static JobResponse fromEntity(final com.procureiq.springboot_app.features.jobs.entity.Job entity) {
        return new JobResponse(
            entity.getId(),
                    entity.getOrganization().getId(),
                    entity.getCategoryId(),
                    entity.getName(),
                    entity.getStatus(),
                    entity.getConfig(),
                    entity.getCreatedAt(),
                    entity.getUpdatedAt()
        );
    }
}
