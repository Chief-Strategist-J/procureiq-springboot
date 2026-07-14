package com.procureiq.springboot_app.api.rest.v1.handlers;

import com.procureiq.springboot_app.features.fieldservice.dto.AssetRelationshipRequest;
import com.procureiq.springboot_app.features.fieldservice.dto.AssetRelationshipResponse;
import com.procureiq.springboot_app.features.fieldservice.service.AssetRelationshipService;
import com.procureiq.springboot_app.shared.types.ApiResponse;
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
    public ResponseEntity<?> createAssetRelationship(@RequestBody AssetRelationshipRequest request) {
        Span span = tracer.spanBuilder("REST.createAssetRelationship").startSpan();
        try (Scope scope = span.makeCurrent()) {
            AssetRelationshipResponse response = assetRelationshipService.createAssetRelationship(request);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(201, response));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @GetMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> getAssetRelationship(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.getAssetRelationship").startSpan();
        try (Scope scope = span.makeCurrent()) {
            AssetRelationshipResponse response = assetRelationshipService.getAssetRelationship(id);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @PutMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> updateAssetRelationship(@PathVariable Long id, @RequestBody AssetRelationshipRequest request) {
        Span span = tracer.spanBuilder("REST.updateAssetRelationship").startSpan();
        try (Scope scope = span.makeCurrent()) {
            AssetRelationshipResponse response = assetRelationshipService.updateAssetRelationship(id, request);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @DeleteMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> deleteAssetRelationship(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.deleteAssetRelationship").startSpan();
        try (Scope scope = span.makeCurrent()) {
            assetRelationshipService.deleteAssetRelationship(id);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, "Deleted asset relationship successfully"));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }
}
