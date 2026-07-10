package com.procureiq.springboot_app.features.fieldservice.dto;

import java.time.LocalDate;

public record ServiceResourceSkillResponse(
    Long id,
    Long serviceResourceId,
    Long skillId,
    Integer skillLevel,
    LocalDate validFrom,
    LocalDate validTo
) {}
