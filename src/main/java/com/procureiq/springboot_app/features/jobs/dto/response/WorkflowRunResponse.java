package com.procureiq.springboot_app.features.jobs.dto.response;

import java.time.Instant;

public record WorkflowRunResponse(
    Long id,
    Long workflowId,
    String status,
    Instant startedAt,
    Instant completedAt,
    Instant createdAt
) {}
