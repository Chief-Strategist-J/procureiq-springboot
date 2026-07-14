package com.procureiq.springboot_app.features.fieldservice.dto.response;

import java.time.LocalDate;

public record MaintenancePlanResponse(
    Long id,
    Long accountId,
    Long assetId,
    Long workTypeId,
    String frequency,
    Integer generationLeadDays,
    LocalDate nextGenerationDate,
    Boolean isActive
) {}
