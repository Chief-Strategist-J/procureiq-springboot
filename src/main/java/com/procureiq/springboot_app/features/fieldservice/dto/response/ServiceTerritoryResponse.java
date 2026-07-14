package com.procureiq.springboot_app.features.fieldservice.dto.response;

public record ServiceTerritoryResponse(
    Long id,
    String name,
    Long parentTerritoryId,
    Long operatingHoursId,
    Boolean isActive
) {}
