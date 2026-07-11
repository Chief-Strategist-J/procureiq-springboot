package com.procureiq.springboot_app.features.campaigns.dto;

import java.time.Instant;

public record CampaignResponse(
    Long id,
    Long orgId,
    String name,
    String status,
    Instant createdAt,
    Instant updatedAt
) {}
