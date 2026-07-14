package com.procureiq.springboot_app.features.fieldservice.dto.request;

public record MilestoneRequest(
    Long entitlementProcessId,
    String name,
    Integer targetMinutes,
    Integer sequence
) {}
