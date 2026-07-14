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
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            ResourcePreferenceResponse response = resourcePreferenceService.createResourcePreference(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(201, response));
        });
    }

    @GetMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> getResourcePreference(@PathVariable Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            ResourcePreferenceResponse response = resourcePreferenceService.getResourcePreference(id);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        });
    }

    @PutMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> updateResourcePreference(@PathVariable Long id, @RequestBody ResourcePreferenceRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            ResourcePreferenceResponse response = resourcePreferenceService.updateResourcePreference(id, request);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        });
    }

    @DeleteMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> deleteResourcePreference(@PathVariable Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            resourcePreferenceService.deleteResourcePreference(id);
            return ResponseEntity.ok(ApiResponse.success(200, "Deleted resource preference successfully"));
        });
    }
}
