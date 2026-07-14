package com.procureiq.springboot_app.api.rest.v1.handlers;

import com.procureiq.springboot_app.features.fieldservice.dto.request.ServiceResourceCapacityRequest;
import com.procureiq.springboot_app.features.fieldservice.dto.response.ServiceResourceCapacityResponse;
import com.procureiq.springboot_app.features.fieldservice.service.ServiceResourceCapacityService;
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
@RequestMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.SERVICE_RESOURCE_CAPACITIES)
@CrossOrigin(origins = "*")
public class ServiceResourceCapacityController {

    private final ServiceResourceCapacityService serviceResourceCapacityService;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public ServiceResourceCapacityController(ServiceResourceCapacityService serviceResourceCapacityService) {
        this.serviceResourceCapacityService = serviceResourceCapacityService;
    }

    @PostMapping
    public ResponseEntity<?> createServiceResourceCapacity(@jakarta.validation.Valid @RequestBody ServiceResourceCapacityRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            ServiceResourceCapacityResponse response = serviceResourceCapacityService.createServiceResourceCapacity(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiSingleResponse.success(201, response));
        });
    }

    @GetMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> getServiceResourceCapacity(@PathVariable Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            ServiceResourceCapacityResponse response = serviceResourceCapacityService.getServiceResourceCapacity(id);
            return ResponseEntity.ok(ApiSingleResponse.success(200, response));
        });
    }

    @PutMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> updateServiceResourceCapacity(@PathVariable Long id, @jakarta.validation.Valid @RequestBody ServiceResourceCapacityRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            ServiceResourceCapacityResponse response = serviceResourceCapacityService.updateServiceResourceCapacity(id, request);
            return ResponseEntity.ok(ApiSingleResponse.success(200, response));
        });
    }

    @DeleteMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> deleteServiceResourceCapacity(@PathVariable Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            serviceResourceCapacityService.deleteServiceResourceCapacity(id);
            return ResponseEntity.ok(ApiSingleResponse.success(200, "Deleted service resource capacity successfully"));
        });
    }
}
