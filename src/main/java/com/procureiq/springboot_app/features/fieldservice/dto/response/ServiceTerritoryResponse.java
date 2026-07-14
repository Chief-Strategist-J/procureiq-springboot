package com.procureiq.springboot_app.features.fieldservice.dto.response;

public record ServiceTerritoryResponse(
    Long id,
    String name,
    Long parentTerritoryId,
    Long operatingHoursId,
    Boolean isActive
) {
    public static ServiceTerritoryResponse fromEntity(final com.procureiq.springboot_app.features.fieldservice.entity.ServiceTerritory entity) {
        return new ServiceTerritoryResponse(
            entity.getId(),
                    entity.getName(),
                    entity.getParentTerritory() != null ? entity.getParentTerritory().getId() : null,
                    entity.getOperatingHours() != null ? entity.getOperatingHours().getId() : null,
                    entity.getIsActive()
        );
    }
}
