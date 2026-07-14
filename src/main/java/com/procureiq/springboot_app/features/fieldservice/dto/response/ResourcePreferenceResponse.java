package com.procureiq.springboot_app.features.fieldservice.dto.response;

public record ResourcePreferenceResponse(
    Long id,
    Long serviceResourceId,
    String relatedRecordType,
    Long relatedRecordId,
    String preferenceType
) {}
