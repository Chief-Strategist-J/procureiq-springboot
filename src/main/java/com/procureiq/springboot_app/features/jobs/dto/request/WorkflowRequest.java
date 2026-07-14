package com.procureiq.springboot_app.features.jobs.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record WorkflowRequest(
    @NotNull(message = "Org ID is required")
    Long orgId,
    @NotBlank(message = "Workflow name is required")
    String name,
    @NotBlank(message = "Status is required")
    String status
) {}
