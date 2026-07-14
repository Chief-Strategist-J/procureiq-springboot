package com.procureiq.springboot_app.features.jobs.dto.request;

import java.util.Map;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record JobRequest(
    @NotNull(message = "Org ID is required")
    Long orgId,
    Long categoryId,
    @NotBlank(message = "Job name is required")
    String name,
    @NotBlank(message = "Status is required")
    String status,
    Map<String, Object> config
) {}
