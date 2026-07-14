package com.procureiq.springboot_app.features.fieldservice.dto.response;

import java.time.Instant;

public record CaseMilestoneResponse(
    Long id,
    Long caseId,
    Long milestoneId,
    Instant startedAt,
    Instant completedAt,
    Boolean isBreached
) {}
