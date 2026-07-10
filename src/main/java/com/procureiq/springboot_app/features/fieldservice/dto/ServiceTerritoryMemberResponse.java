package com.procureiq.springboot_app.features.fieldservice.dto;

public record ServiceTerritoryMemberResponse(
    Long id,
    Long serviceTerritoryId,
    Long serviceResourceId,
    Long operatingHoursId,
    String territoryType,
    String travelMode
) {}
