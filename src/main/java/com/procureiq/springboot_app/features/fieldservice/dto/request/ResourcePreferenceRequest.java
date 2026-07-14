package com.procureiq.springboot_app.features.fieldservice.dto.request;

public record ResourcePreferenceRequest(
    Long serviceResourceId,
    String relatedRecordType,
    Long relatedRecordId,
    String preferenceType
) {}
