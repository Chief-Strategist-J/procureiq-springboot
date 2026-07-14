package com.procureiq.springboot_app.features.campaigns.dto.response;

import java.time.Instant;
import com.procureiq.springboot_app.features.campaigns.entity.Campaign;

public record CampaignResponse(
    Long id,
    Long orgId,
    String name,
    String status,
    Instant createdAt,
    Instant updatedAt
) {
    public static CampaignResponse fromEntity(final Campaign c) {
        return new CampaignResponse(
            c.getId(),
            c.getOrganization() != null ? c.getOrganization().getId() : null,
            c.getName(),
            c.getStatus(),
            c.getCreatedAt(),
            c.getUpdatedAt()
        );
    }
}
