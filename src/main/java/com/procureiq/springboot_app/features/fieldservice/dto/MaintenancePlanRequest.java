package com.procureiq.springboot_app.features.fieldservice.dto;

import java.time.LocalDate;

public record MaintenancePlanRequest(
    Long accountId,
    Long assetId,
    Long workTypeId,
    String frequency,
    Integer generationLeadDays,
    LocalDate nextGenerationDate,
    Boolean isActive
) {}
