package com.procureiq.springboot_app.features.jobs.dto.response;

import java.time.Instant;

public record JobRunResponse(
    Long id,
    Long jobId,
    String status,
    Instant startedAt,
    Instant completedAt,
    Instant createdAt
) {
    public static JobRunResponse fromEntity(final com.procureiq.springboot_app.features.jobs.entity.JobRun entity) {
        return new JobRunResponse(
            entity.getId(),
                    entity.getJobId(),
                    entity.getStatus(),
                    entity.getStartedAt(),
                    entity.getCompletedAt(),
                    entity.getCreatedAt()
        );
    }
}
