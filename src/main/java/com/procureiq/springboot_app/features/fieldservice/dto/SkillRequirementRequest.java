package com.procureiq.springboot_app.features.fieldservice.dto;

public record SkillRequirementRequest(
    Long skillId,
    String requiredForType,
    Long requiredForId,
    Integer minSkillLevel
) {}
