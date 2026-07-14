package com.procureiq.springboot_app.features.fieldservice.dto.response;

public record OperatingHoursResponse(
    Long id,
    String name,
    String timezone
) {
    public static OperatingHoursResponse fromEntity(final com.procureiq.springboot_app.features.fieldservice.entity.OperatingHours entity) {
        return new OperatingHoursResponse(
            entity.getId(), entity.getName(), entity.getTimezone()
        );
    }
}
