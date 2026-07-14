package com.procureiq.springboot_app.features.fieldservice.dto.response;

import java.time.LocalTime;

public record TimeSlotResponse(
    Long id,
    Long operatingHoursId,
    Integer dayOfWeek,
    LocalTime startTime,
    LocalTime endTime
) {}
