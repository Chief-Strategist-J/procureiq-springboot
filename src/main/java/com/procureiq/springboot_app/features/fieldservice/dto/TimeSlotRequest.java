package com.procureiq.springboot_app.features.fieldservice.dto;

import java.time.LocalTime;

public record TimeSlotRequest(
    Long operatingHoursId,
    Integer dayOfWeek,
    LocalTime startTime,
    LocalTime endTime
) {}
