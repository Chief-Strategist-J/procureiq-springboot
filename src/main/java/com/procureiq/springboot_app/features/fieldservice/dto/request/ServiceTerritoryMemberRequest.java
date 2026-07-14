package com.procureiq.springboot_app.features.fieldservice.dto.request;

public record ServiceTerritoryMemberRequest(
    Long serviceTerritoryId,
    Long serviceResourceId,
    Long operatingHoursId,
    String territoryType,
    String travelMode
) {}
