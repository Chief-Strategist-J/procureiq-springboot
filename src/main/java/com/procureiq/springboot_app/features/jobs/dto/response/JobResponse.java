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
) {}
