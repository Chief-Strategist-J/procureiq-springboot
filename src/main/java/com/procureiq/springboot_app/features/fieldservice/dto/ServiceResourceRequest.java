package com.procureiq.springboot_app.features.fieldservice.dto;

public record ServiceResourceRequest(
    String name,
    Long userId,
    Long serviceCrewId,
    String resourceType,
    Boolean isActive
) {}
