package com.procureiq.springboot_app.features.fieldservice.dto;

import java.time.Instant;

public record ShiftRequest(
    Long serviceResourceId,
    Long serviceTerritoryId,
    Instant startTime,
    Instant endTime,
    String shiftType
) {}
