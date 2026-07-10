package com.procureiq.springboot_app.features.fieldservice.dto;

public record ResourcePreferenceResponse(
    Long id,
    Long serviceResourceId,
    String relatedRecordType,
    Long relatedRecordId,
    String preferenceType
) {}
