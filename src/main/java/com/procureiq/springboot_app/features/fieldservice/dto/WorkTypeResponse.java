package com.procureiq.springboot_app.features.fieldservice.dto;

public record WorkTypeResponse(
    Long id,
    String name,
    Integer defaultDurationMinutes,
    Integer estimatedTravelMinutes
) {}
