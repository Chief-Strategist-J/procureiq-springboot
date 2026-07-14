package com.procureiq.springboot_app.features.fieldservice.dto.response;

import java.time.Instant;

public record AssignedResourceResponse(
    Long id,
    Long serviceAppointmentId,
    Instant serviceAppointmentCreatedAt,
    Long serviceResourceId,
    Long serviceCrewId,
    Boolean isPrimaryResource,
    Instant assignedAt,
    String status
) {}
