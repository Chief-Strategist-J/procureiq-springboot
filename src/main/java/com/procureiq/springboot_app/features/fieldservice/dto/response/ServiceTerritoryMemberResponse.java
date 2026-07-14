package com.procureiq.springboot_app.features.fieldservice.dto.response;

public record ServiceTerritoryMemberResponse(
    Long id,
    Long serviceTerritoryId,
    Long serviceResourceId,
    Long operatingHoursId,
    String territoryType,
    String travelMode
) {}
