package com.procureiq.springboot_app.api.rest.v1.handlers;

import com.procureiq.springboot_app.features.fieldservice.dto.request.ResourceAbsenceRequest;
import com.procureiq.springboot_app.features.fieldservice.dto.response.ResourceAbsenceResponse;
import com.procureiq.springboot_app.features.fieldservice.service.ResourceAbsenceService;
import com.procureiq.springboot_app.shared.types.single.ApiSingleResponse;
import com.procureiq.springboot_app.shared.types.list.ApiListResponse;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.RESOURCE_ABSENCES)
@CrossOrigin(origins = "*")
public class ResourceAbsenceController {

    private final ResourceAbsenceService resourceAbsenceService;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public ResourceAbsenceController(ResourceAbsenceService resourceAbsenceService) {
        this.resourceAbsenceService = resourceAbsenceService;
    }

    @PostMapping
    public ResponseEntity<?> createResourceAbsence(@jakarta.validation.Valid @RequestBody ResourceAbsenceRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            ResourceAbsenceResponse response = resourceAbsenceService.createResourceAbsence(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiSingleResponse.success(201, response));
        });
    }

    @GetMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> getResourceAbsence(@PathVariable Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            ResourceAbsenceResponse response = resourceAbsenceService.getResourceAbsence(id);
            return ResponseEntity.ok(ApiSingleResponse.success(200, response));
        });
    }

    @PutMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> updateResourceAbsence(@PathVariable Long id, @jakarta.validation.Valid @RequestBody ResourceAbsenceRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            ResourceAbsenceResponse response = resourceAbsenceService.updateResourceAbsence(id, request);
            return ResponseEntity.ok(ApiSingleResponse.success(200, response));
        });
    }

    @DeleteMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> deleteResourceAbsence(@PathVariable Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            resourceAbsenceService.deleteResourceAbsence(id);
            return ResponseEntity.ok(ApiSingleResponse.success(200, "Deleted resource absence successfully"));
        });
    }
}
