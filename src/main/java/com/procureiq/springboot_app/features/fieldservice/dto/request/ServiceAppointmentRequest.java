package com.procureiq.springboot_app.features.fieldservice.dto.request;

import java.time.Instant;
import java.util.Map;

public record ServiceAppointmentRequest(
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
    Map<String, Object> address
) {}
