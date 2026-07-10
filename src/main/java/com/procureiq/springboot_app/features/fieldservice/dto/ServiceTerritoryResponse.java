package com.procureiq.springboot_app.features.fieldservice.dto;

public record ServiceTerritoryResponse(
    Long id,
    String name,
    Long parentTerritoryId,
    Long operatingHoursId,
    Boolean isActive
) {}
