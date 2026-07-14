package com.procureiq.springboot_app.features.fieldservice.dto.response;

import java.time.Instant;

public record ShiftResponse(
    Long id,
    Long serviceResourceId,
    Long serviceTerritoryId,
    Instant startTime,
    Instant endTime,
    String shiftType
) {}
