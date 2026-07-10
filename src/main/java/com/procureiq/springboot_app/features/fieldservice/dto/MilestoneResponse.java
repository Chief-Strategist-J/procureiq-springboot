package com.procureiq.springboot_app.features.fieldservice.dto;

public record MilestoneResponse(
    Long id,
    Long entitlementProcessId,
    String name,
    Integer targetMinutes,
    Integer sequence
) {}
