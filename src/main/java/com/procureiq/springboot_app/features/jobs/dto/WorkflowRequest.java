package com.procureiq.springboot_app.features.jobs.dto;

public record WorkflowRequest(
    Long orgId,
    String name,
    String status
) {}
