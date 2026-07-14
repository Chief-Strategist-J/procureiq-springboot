package com.procureiq.springboot_app.features.fieldservice.dto.response;

public record ServiceResourceResponse(
    Long id,
    String name,
    Long userId,
    Long serviceCrewId,
    String resourceType,
    Boolean isActive
) {
    public static ServiceResourceResponse fromEntity(final com.procureiq.springboot_app.features.fieldservice.entity.ServiceResource entity) {
        return new ServiceResourceResponse(
            entity.getId(),
                            entity.getName(),
                            entity.getUser() != null ? entity.getUser().getId() : null,
                            entity.getServiceCrew() != null ? entity.getServiceCrew().getId() : null,
                            entity.getResourceType(),
                            entity.getIsActive()
        );
    }
}
