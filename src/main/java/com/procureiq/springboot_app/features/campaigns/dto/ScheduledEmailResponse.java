package com.procureiq.springboot_app.features.campaigns.dto;

import java.time.Instant;

public record ScheduledEmailResponse(
    Long id,
    Long orgId,
    Long campaignId,
    Long contactId,
    Long templateId,
    Instant scheduledAt,
    String status,
    Instant createdAt,
    Instant updatedAt
) {}
