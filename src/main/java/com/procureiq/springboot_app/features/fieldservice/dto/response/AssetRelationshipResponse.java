package com.procureiq.springboot_app.features.fieldservice.dto.response;

public record AssetRelationshipResponse(
    Long id,
    Long assetId,
    Long relatedAssetId,
    String relationshipType
) {}
