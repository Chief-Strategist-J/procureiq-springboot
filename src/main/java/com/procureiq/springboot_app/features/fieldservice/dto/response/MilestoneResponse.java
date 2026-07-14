package com.procureiq.springboot_app.features.fieldservice.dto.response;

public record MilestoneResponse(
    Long id,
    Long entitlementProcessId,
    String name,
    Integer targetMinutes,
    Integer sequence
) {}
