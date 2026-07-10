package com.procureiq.springboot_app.features.fieldservice.dto;

import java.time.Instant;

public record ResourceAbsenceRequest(
    Long serviceResourceId,
    String absenceType,
    Instant startTime,
    Instant endTime,
    String status
) {}
