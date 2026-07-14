package com.procureiq.springboot_app.features.fieldservice.dto.response;

import java.time.LocalTime;

public record TimeSlotResponse(
    Long id,
    Long operatingHoursId,
    Integer dayOfWeek,
    LocalTime startTime,
    LocalTime endTime
) {
    public static TimeSlotResponse fromEntity(final com.procureiq.springboot_app.features.fieldservice.entity.TimeSlot entity) {
        return new TimeSlotResponse(
            entity.getId(),
            entity.getOperatingHours().getId(),
            entity.getDayOfWeek(),
            entity.getStartTime(),
            entity.getEndTime()
        );
    }
}
