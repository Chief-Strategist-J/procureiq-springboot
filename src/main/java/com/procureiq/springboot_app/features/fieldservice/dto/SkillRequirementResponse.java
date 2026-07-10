package com.procureiq.springboot_app.features.fieldservice.dto;

public record SkillRequirementResponse(
    Long id,
    Long skillId,
    String requiredForType,
    Long requiredForId,
    Integer minSkillLevel
) {}
