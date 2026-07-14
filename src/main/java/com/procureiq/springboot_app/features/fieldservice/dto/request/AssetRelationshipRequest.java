package com.procureiq.springboot_app.features.fieldservice.dto.request;

public record AssetRelationshipRequest(
    Long assetId,
    Long relatedAssetId,
    String relationshipType
) {}
