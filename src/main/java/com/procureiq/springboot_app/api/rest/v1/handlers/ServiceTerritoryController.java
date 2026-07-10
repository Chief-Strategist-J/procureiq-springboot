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
@RequestMapping("/api/v1/fieldservice/territories")
@CrossOrigin(origins = "*")
public class ServiceTerritoryController {

    private final ServiceTerritoryService serviceTerritoryService;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public ServiceTerritoryController(ServiceTerritoryService serviceTerritoryService) {
        this.serviceTerritoryService = serviceTerritoryService;
    }

    @GetMapping
    public ResponseEntity<?> getAllServiceTerritories() {
        Span span = tracer.spanBuilder("REST.getAllServiceTerritories").startSpan();
        try (Scope scope = span.makeCurrent()) {
            java.util.List<ServiceTerritoryResponse> response = serviceTerritoryService.getAllServiceTerritories();
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(500, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @PostMapping
    public ResponseEntity<?> createServiceTerritory(@RequestBody ServiceTerritoryRequest request) {
        Span span = tracer.spanBuilder("REST.createServiceTerritory").startSpan();
        try (Scope scope = span.makeCurrent()) {
            ServiceTerritoryResponse response = serviceTerritoryService.createServiceTerritory(request);
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
    public ResponseEntity<?> getServiceTerritory(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.getServiceTerritory").startSpan();
        try (Scope scope = span.makeCurrent()) {
            ServiceTerritoryResponse response = serviceTerritoryService.getServiceTerritory(id);
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
    public ResponseEntity<?> updateServiceTerritory(@PathVariable Long id, @RequestBody ServiceTerritoryRequest request) {
        Span span = tracer.spanBuilder("REST.updateServiceTerritory").startSpan();
        try (Scope scope = span.makeCurrent()) {
            ServiceTerritoryResponse response = serviceTerritoryService.updateServiceTerritory(id, request);
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
    public ResponseEntity<?> deleteServiceTerritory(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.deleteServiceTerritory").startSpan();
        try (Scope scope = span.makeCurrent()) {
            serviceTerritoryService.deleteServiceTerritory(id);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, "Deleted territory successfully"));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }
}
