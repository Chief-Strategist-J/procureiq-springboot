package com.procureiq.springboot_app.features.githubactions.dto.response;

import java.time.Instant;

public record GithubActionTemplateResponse(
    Long id,
    String name,
    String category,
    String description,
    String cronExpression,
    String eventType,
    String yamlContent,
    Instant createdAt,
    Instant updatedAt
) {}
