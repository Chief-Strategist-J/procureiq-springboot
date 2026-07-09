package com.procureiq.springboot_app.features.notifications.dto;

import java.time.Instant;
import java.util.Map;

public record SendNotificationRequest(
    String typeCode,
    String sourceService,
    String targetScope,
    Long targetId,
    String dedupKey,
    Map<String, Object> payload,
    Map<String, Object> metadata,
    Integer priority,
    Instant scheduledFor
) {}
