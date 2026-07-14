package com.procureiq.springboot_app.api.rest.v1.handlers;

import com.procureiq.springboot_app.features.fieldservice.dto.request.AssetRelationshipRequest;
import com.procureiq.springboot_app.features.fieldservice.dto.response.AssetRelationshipResponse;
import com.procureiq.springboot_app.features.fieldservice.service.AssetRelationshipService;
import com.procureiq.springboot_app.shared.types.ApiSingleResponse;
import com.procureiq.springboot_app.shared.types.ApiListResponse;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.ASSET_RELATIONSHIPS)
@CrossOrigin(origins = "*")
public class AssetRelationshipController {

    private final AssetRelationshipService assetRelationshipService;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public AssetRelationshipController(AssetRelationshipService assetRelationshipService) {
        this.assetRelationshipService = assetRelationshipService;
    }

    @PostMapping
    public ResponseEntity<?> createAssetRelationship(@jakarta.validation.Valid @RequestBody AssetRelationshipRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            AssetRelationshipResponse response = assetRelationshipService.createAssetRelationship(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiSingleResponse.success(201, response));
        });
    }

    @GetMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> getAssetRelationship(@PathVariable Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            AssetRelationshipResponse response = assetRelationshipService.getAssetRelationship(id);
            return ResponseEntity.ok(ApiSingleResponse.success(200, response));
        });
    }

    @PutMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> updateAssetRelationship(@PathVariable Long id, @jakarta.validation.Valid @RequestBody AssetRelationshipRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            AssetRelationshipResponse response = assetRelationshipService.updateAssetRelationship(id, request);
            return ResponseEntity.ok(ApiSingleResponse.success(200, response));
        });
    }

    @DeleteMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> deleteAssetRelationship(@PathVariable Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            assetRelationshipService.deleteAssetRelationship(id);
            return ResponseEntity.ok(ApiSingleResponse.success(200, "Deleted asset relationship successfully"));
        });
    }
}
