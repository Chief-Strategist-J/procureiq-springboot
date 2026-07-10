package com.procureiq.springboot_app.features.fieldservice.dto;

public record ServiceTerritoryRequest(
    String name,
    Long parentTerritoryId,
    Long operatingHoursId,
    Boolean isActive
) {}
