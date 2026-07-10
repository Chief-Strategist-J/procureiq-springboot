package com.procureiq.springboot_app.features.fieldservice.service;

import com.procureiq.springboot_app.features.fieldservice.dto.AssetRelationshipRequest;
import com.procureiq.springboot_app.features.fieldservice.dto.AssetRelationshipResponse;
import com.procureiq.springboot_app.features.fieldservice.entity.Asset;
import com.procureiq.springboot_app.features.fieldservice.entity.AssetRelationship;
import com.procureiq.springboot_app.features.fieldservice.repository.AssetRepository;
import com.procureiq.springboot_app.features.fieldservice.repository.AssetRelationshipRepository;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AssetRelationshipService {

    private final AssetRelationshipRepository assetRelationshipRepository;
    private final AssetRepository assetRepository;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public AssetRelationshipService(AssetRelationshipRepository assetRelationshipRepository, AssetRepository assetRepository) {
        this.assetRelationshipRepository = assetRelationshipRepository;
        this.assetRepository = assetRepository;
    }

    @Transactional
    public AssetRelationshipResponse createAssetRelationship(AssetRelationshipRequest request) {
        Span span = tracer.spanBuilder("AssetRelationshipService.createAssetRelationship").startSpan();
        try {
            Asset asset = assetRepository.findById(request.assetId())
                    .orElseThrow(() -> new IllegalArgumentException("Asset not found: " + request.assetId()));
            Asset relatedAsset = assetRepository.findById(request.relatedAssetId())
                    .orElseThrow(() -> new IllegalArgumentException("Related Asset not found: " + request.relatedAssetId()));

            AssetRelationship ar = new AssetRelationship();
            ar.setAsset(asset);
            ar.setRelatedAsset(relatedAsset);
            ar.setRelationshipType(request.relationshipType());
            ar = assetRelationshipRepository.save(ar);

            return mapToResponse(ar);
        } finally {
            span.end();
        }
    }

    @Transactional(readOnly = true)
    public AssetRelationshipResponse getAssetRelationship(Long id) {
        Span span = tracer.spanBuilder("AssetRelationshipService.getAssetRelationship").startSpan();
        try {
            AssetRelationship ar = assetRelationshipRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("AssetRelationship not found: " + id));
            return mapToResponse(ar);
        } finally {
            span.end();
        }
    }

    @Transactional
    public AssetRelationshipResponse updateAssetRelationship(Long id, AssetRelationshipRequest request) {
        Span span = tracer.spanBuilder("AssetRelationshipService.updateAssetRelationship").startSpan();
        try {
            AssetRelationship ar = assetRelationshipRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("AssetRelationship not found: " + id));

            Asset asset = assetRepository.findById(request.assetId())
                    .orElseThrow(() -> new IllegalArgumentException("Asset not found: " + request.assetId()));
            Asset relatedAsset = assetRepository.findById(request.relatedAssetId())
                    .orElseThrow(() -> new IllegalArgumentException("Related Asset not found: " + request.relatedAssetId()));

            ar.setAsset(asset);
            ar.setRelatedAsset(relatedAsset);
            ar.setRelationshipType(request.relationshipType());
            ar = assetRelationshipRepository.save(ar);

            return mapToResponse(ar);
        } finally {
            span.end();
        }
    }

    @Transactional
    public void deleteAssetRelationship(Long id) {
        Span span = tracer.spanBuilder("AssetRelationshipService.deleteAssetRelationship").startSpan();
        try {
            assetRelationshipRepository.deleteById(id);
        } finally {
            span.end();
        }
    }

    private AssetRelationshipResponse mapToResponse(AssetRelationship ar) {
        return new AssetRelationshipResponse(
            ar.getId(),
            ar.getAsset().getId(),
            ar.getRelatedAsset().getId(),
            ar.getRelationshipType()
        );
    }
}
