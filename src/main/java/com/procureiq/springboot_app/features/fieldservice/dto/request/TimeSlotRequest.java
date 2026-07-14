package com.procureiq.springboot_app.features.fieldservice.dto.request;

import java.time.LocalTime;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

public record TimeSlotRequest(
    @NotNull(message = "Operating Hours ID is required")
    Long operatingHoursId,

    @NotNull(message = "Day of week is required")
    @Min(value = 1, message = "Day of week must be between 1 and 7")
    @Max(value = 7, message = "Day of week must be between 1 and 7")
    Integer dayOfWeek,

    @NotNull(message = "Start time is required")
    LocalTime startTime,

    @NotNull(message = "End time is required")
    LocalTime endTime
) {}
