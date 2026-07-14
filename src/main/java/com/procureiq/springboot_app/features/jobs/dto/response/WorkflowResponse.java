package com.procureiq.springboot_app.features.jobs.dto.response;

import java.time.Instant;

public record WorkflowResponse(
    Long id,
    Long orgId,
    String name,
    String status,
    Instant createdAt,
    Instant updatedAt
) {}
