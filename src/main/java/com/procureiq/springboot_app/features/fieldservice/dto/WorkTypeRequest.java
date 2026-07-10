package com.procureiq.springboot_app.features.fieldservice.dto;

public record WorkTypeRequest(
    String name,
    Integer defaultDurationMinutes,
    Integer estimatedTravelMinutes
) {}
