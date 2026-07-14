package com.procureiq.springboot_app.api.rest.v1.handlers;

import com.procureiq.springboot_app.features.fieldservice.dto.ServiceResourceCapacityRequest;
import com.procureiq.springboot_app.features.fieldservice.dto.ServiceResourceCapacityResponse;
import com.procureiq.springboot_app.features.fieldservice.service.ServiceResourceCapacityService;
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
@RequestMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.SERVICE_RESOURCE_CAPACITIES)
@CrossOrigin(origins = "*")
public class ServiceResourceCapacityController {

    private final ServiceResourceCapacityService serviceResourceCapacityService;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public ServiceResourceCapacityController(ServiceResourceCapacityService serviceResourceCapacityService) {
        this.serviceResourceCapacityService = serviceResourceCapacityService;
    }

    @PostMapping
    public ResponseEntity<?> createServiceResourceCapacity(@RequestBody ServiceResourceCapacityRequest request) {
        Span span = tracer.spanBuilder("REST.createServiceResourceCapacity").startSpan();
        try (Scope scope = span.makeCurrent()) {
            ServiceResourceCapacityResponse response = serviceResourceCapacityService.createServiceResourceCapacity(request);
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
    public ResponseEntity<?> getServiceResourceCapacity(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.getServiceResourceCapacity").startSpan();
        try (Scope scope = span.makeCurrent()) {
            ServiceResourceCapacityResponse response = serviceResourceCapacityService.getServiceResourceCapacity(id);
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
    public ResponseEntity<?> updateServiceResourceCapacity(@PathVariable Long id, @RequestBody ServiceResourceCapacityRequest request) {
        Span span = tracer.spanBuilder("REST.updateServiceResourceCapacity").startSpan();
        try (Scope scope = span.makeCurrent()) {
            ServiceResourceCapacityResponse response = serviceResourceCapacityService.updateServiceResourceCapacity(id, request);
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
    public ResponseEntity<?> deleteServiceResourceCapacity(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.deleteServiceResourceCapacity").startSpan();
        try (Scope scope = span.makeCurrent()) {
            serviceResourceCapacityService.deleteServiceResourceCapacity(id);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, "Deleted service resource capacity successfully"));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }
}
