package com.procureiq.springboot_app.features.fieldservice.dto.request;

import java.time.Instant;

public record CaseMilestoneRequest(
    Long caseId,
    Long milestoneId,
    Instant startedAt,
    Instant completedAt,
    Boolean isBreached
) {}
