package com.procureiq.springboot_app.features.campaigns.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CampaignRequest(
    @NotNull(message = "Org ID is required")
    Long orgId,
    @NotBlank(message = "Campaign name is required")
    String name,
    @NotBlank(message = "Status is required")
    String status
) {}
