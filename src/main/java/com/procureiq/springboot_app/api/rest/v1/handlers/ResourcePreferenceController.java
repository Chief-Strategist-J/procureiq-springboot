package com.procureiq.springboot_app.api.rest.v1.handlers;

import com.procureiq.springboot_app.features.fieldservice.dto.ResourcePreferenceRequest;
import com.procureiq.springboot_app.features.fieldservice.dto.ResourcePreferenceResponse;
import com.procureiq.springboot_app.features.fieldservice.service.ResourcePreferenceService;
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
@RequestMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.RESOURCE_PREFERENCES)
@CrossOrigin(origins = "*")
public class ResourcePreferenceController {

    private final ResourcePreferenceService resourcePreferenceService;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public ResourcePreferenceController(ResourcePreferenceService resourcePreferenceService) {
        this.resourcePreferenceService = resourcePreferenceService;
    }

    @PostMapping
    public ResponseEntity<?> createResourcePreference(@RequestBody ResourcePreferenceRequest request) {
        Span span = tracer.spanBuilder("REST.createResourcePreference").startSpan();
        try (Scope scope = span.makeCurrent()) {
            ResourcePreferenceResponse response = resourcePreferenceService.createResourcePreference(request);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(201, response));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getResourcePreference(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.getResourcePreference").startSpan();
        try (Scope scope = span.makeCurrent()) {
            ResourcePreferenceResponse response = resourcePreferenceService.getResourcePreference(id);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateResourcePreference(@PathVariable Long id, @RequestBody ResourcePreferenceRequest request) {
        Span span = tracer.spanBuilder("REST.updateResourcePreference").startSpan();
        try (Scope scope = span.makeCurrent()) {
            ResourcePreferenceResponse response = resourcePreferenceService.updateResourcePreference(id, request);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteResourcePreference(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.deleteResourcePreference").startSpan();
        try (Scope scope = span.makeCurrent()) {
            resourcePreferenceService.deleteResourcePreference(id);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, "Deleted resource preference successfully"));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }
}
