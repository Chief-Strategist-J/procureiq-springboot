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
        Span span = tracer.spanBuilder("REST.createMaintenancePlan").startSpan();
        try (Scope scope = span.makeCurrent()) {
            MaintenancePlanResponse response = maintenancePlanService.createMaintenancePlan(request);
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
    public ResponseEntity<?> getMaintenancePlan(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.getMaintenancePlan").startSpan();
        try (Scope scope = span.makeCurrent()) {
            MaintenancePlanResponse response = maintenancePlanService.getMaintenancePlan(id);
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
    public ResponseEntity<?> updateMaintenancePlan(@PathVariable Long id, @RequestBody MaintenancePlanRequest request) {
        Span span = tracer.spanBuilder("REST.updateMaintenancePlan").startSpan();
        try (Scope scope = span.makeCurrent()) {
            MaintenancePlanResponse response = maintenancePlanService.updateMaintenancePlan(id, request);
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
    public ResponseEntity<?> deleteMaintenancePlan(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.deleteMaintenancePlan").startSpan();
        try (Scope scope = span.makeCurrent()) {
            maintenancePlanService.deleteMaintenancePlan(id);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, "Deleted maintenance plan successfully"));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }
}
