package com.procureiq.springboot_app.features.fieldservice.dto.request;

public record SkillRequirementRequest(
    Long skillId,
    String requiredForType,
    Long requiredForId,
    Integer minSkillLevel
) {}
