package com.procureiq.springboot_app.features.fieldservice.dto.request;

import java.time.LocalDate;

public record ServiceResourceSkillRequest(
    Long serviceResourceId,
    Long skillId,
    Integer skillLevel,
    LocalDate validFrom,
    LocalDate validTo
) {}
