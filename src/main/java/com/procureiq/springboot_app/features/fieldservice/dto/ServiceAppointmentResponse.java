package com.procureiq.springboot_app.features.fieldservice.dto;

import java.time.Instant;
import java.util.Map;

public record ServiceAppointmentResponse(
    Long id,
    String parentRecordType,
    Long parentRecordId,
    Long accountId,
    Long contactId,
    Long serviceTerritoryId,
    Long workTypeId,
    String status,
    Instant scheduledStart,
    Instant scheduledEnd,
    Instant arrivalWindowStart,
    Instant arrivalWindowEnd,
    Integer durationMinutes,
    Map<String, Object> address,
    Instant createdAt
) {}
