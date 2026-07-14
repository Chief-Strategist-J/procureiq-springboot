package com.procureiq.springboot_app.api.rest.v1.handlers;

import com.procureiq.springboot_app.features.fieldservice.dto.MaintenancePlanRequest;
import com.procureiq.springboot_app.features.fieldservice.dto.MaintenancePlanResponse;
import com.procureiq.springboot_app.features.fieldservice.service.MaintenancePlanService;
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
@RequestMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.MAINTENANCE_PLANS)
@CrossOrigin(origins = "*")
public class MaintenancePlanController {

    private final MaintenancePlanService maintenancePlanService;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public MaintenancePlanController(MaintenancePlanService maintenancePlanService) {
        this.maintenancePlanService = maintenancePlanService;
    }

    @PostMapping
    public ResponseEntity<?> createMaintenancePlan(@RequestBody MaintenancePlanRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            MaintenancePlanResponse response = maintenancePlanService.createMaintenancePlan(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(201, response));
        });
    }

    @GetMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> getMaintenancePlan(@PathVariable Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            MaintenancePlanResponse response = maintenancePlanService.getMaintenancePlan(id);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        });
    }

    @PutMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> updateMaintenancePlan(@PathVariable Long id, @RequestBody MaintenancePlanRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            MaintenancePlanResponse response = maintenancePlanService.updateMaintenancePlan(id, request);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        });
    }

    @DeleteMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> deleteMaintenancePlan(@PathVariable Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            maintenancePlanService.deleteMaintenancePlan(id);
            return ResponseEntity.ok(ApiResponse.success(200, "Deleted maintenance plan successfully"));
        });
    }
}
