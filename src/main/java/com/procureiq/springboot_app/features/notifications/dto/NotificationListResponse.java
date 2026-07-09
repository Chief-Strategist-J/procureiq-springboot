package com.procureiq.springboot_app.features.notifications.dto;

import java.util.List;

public record NotificationListResponse(
    List<NotificationResponse> content,
    int page,
    int size,
    long totalElements
) {}
