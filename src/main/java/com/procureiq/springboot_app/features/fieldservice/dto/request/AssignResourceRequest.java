package com.procureiq.springboot_app.features.fieldservice.dto.request;

import java.time.Instant;

public record AssignResourceRequest(
    Long serviceAppointmentId,
    Instant serviceAppointmentCreatedAt,
    Long serviceResourceId,
    Long serviceCrewId,
    Boolean isPrimaryResource,
    String status
) {}
