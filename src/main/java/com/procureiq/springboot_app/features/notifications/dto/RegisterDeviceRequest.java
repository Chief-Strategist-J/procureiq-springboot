package com.procureiq.springboot_app.features.notifications.dto;

import java.util.Map;

public record RegisterDeviceRequest(
    String platform,
    String pushToken,
    Map<String, Object> webPushEndpoint,
    String appVersion,
    String osVersion,
    Map<String, Object> capabilities
) {}
