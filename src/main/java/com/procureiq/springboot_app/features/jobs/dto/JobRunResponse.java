package com.procureiq.springboot_app.features.jobs.dto;

import java.time.Instant;

public record JobRunResponse(
    Long id,
    Long jobId,
    String status,
    Instant startedAt,
    Instant completedAt,
    Instant createdAt
) {}
