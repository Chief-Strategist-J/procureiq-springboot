package com.procureiq.springboot_app.features.jobs.dto;

import java.util.Map;

public record JobRequest(
    Long orgId,
    Long categoryId,
    String name,
    String status,
    Map<String, Object> config
) {}
