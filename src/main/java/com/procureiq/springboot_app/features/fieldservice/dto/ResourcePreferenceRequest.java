package com.procureiq.springboot_app.features.fieldservice.dto;

public record ResourcePreferenceRequest(
    Long serviceResourceId,
    String relatedRecordType,
    Long relatedRecordId,
    String preferenceType
) {}
