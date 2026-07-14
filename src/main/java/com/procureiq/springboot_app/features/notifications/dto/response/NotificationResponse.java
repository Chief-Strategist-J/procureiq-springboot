package com.procureiq.springboot_app.features.notifications.dto.response;

import java.time.Instant;
import java.util.Map;

public record NotificationResponse(
    Long id,
    String typeCode,
    String sourceService,
    Map<String, Object> payload,
    Map<String, Object> metadata,
    Integer priority,
    String targetScope,
    String status,
    Instant createdAt
) {}
