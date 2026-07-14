package com.procureiq.springboot_app.features.fieldservice.dto.response;

public record WorkTypeResponse(
    Long id,
    String name,
    Integer defaultDurationMinutes,
    Integer estimatedTravelMinutes
) {}
