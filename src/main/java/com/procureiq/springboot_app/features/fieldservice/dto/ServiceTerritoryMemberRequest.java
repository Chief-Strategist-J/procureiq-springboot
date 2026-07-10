package com.procureiq.springboot_app.features.fieldservice.dto;

public record ServiceTerritoryMemberRequest(
    Long serviceTerritoryId,
    Long serviceResourceId,
    Long operatingHoursId,
    String territoryType,
    String travelMode
) {}
