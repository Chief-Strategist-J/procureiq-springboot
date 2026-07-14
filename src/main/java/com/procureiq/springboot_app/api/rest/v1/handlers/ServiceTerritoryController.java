package com.procureiq.springboot_app.api.rest.v1.handlers;

import com.procureiq.springboot_app.features.fieldservice.dto.ServiceTerritoryRequest;
import com.procureiq.springboot_app.features.fieldservice.dto.ServiceTerritoryResponse;
import com.procureiq.springboot_app.features.fieldservice.service.ServiceTerritoryService;
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
@RequestMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.TERRITORIES)
@CrossOrigin(origins = "*")
public class ServiceTerritoryController {

    private final ServiceTerritoryService serviceTerritoryService;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public ServiceTerritoryController(ServiceTerritoryService serviceTerritoryService) {
        this.serviceTerritoryService = serviceTerritoryService;
    }

    @GetMapping
    public ResponseEntity<?> getAllServiceTerritories() {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            java.util.List<ServiceTerritoryResponse> response = serviceTerritoryService.getAllServiceTerritories();
            return ResponseEntity.ok(ApiResponse.success(200, response));
        });
    }

    @PostMapping
    public ResponseEntity<?> createServiceTerritory(@jakarta.validation.Valid @RequestBody ServiceTerritoryRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            ServiceTerritoryResponse response = serviceTerritoryService.createServiceTerritory(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(201, response));
        });
    }

    @GetMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> getServiceTerritory(@PathVariable Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            ServiceTerritoryResponse response = serviceTerritoryService.getServiceTerritory(id);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        });
    }

    @PutMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> updateServiceTerritory(@PathVariable Long id, @jakarta.validation.Valid @RequestBody ServiceTerritoryRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            ServiceTerritoryResponse response = serviceTerritoryService.updateServiceTerritory(id, request);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        });
    }

    @DeleteMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> deleteServiceTerritory(@PathVariable Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            serviceTerritoryService.deleteServiceTerritory(id);
            return ResponseEntity.ok(ApiResponse.success(200, "Deleted territory successfully"));
        });
    }
}
