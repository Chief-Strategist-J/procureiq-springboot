package com.procureiq.springboot_app.features.fieldservice.dto;

public record AssetRelationshipRequest(
    Long assetId,
    Long relatedAssetId,
    String relationshipType
) {}
