package com.procureiq.springboot_app.api.rest.v1.handlers;

import com.procureiq.springboot_app.features.fieldservice.dto.ServiceResourceRequest;
import com.procureiq.springboot_app.features.fieldservice.dto.ServiceResourceResponse;
import com.procureiq.springboot_app.features.fieldservice.service.ServiceResourceService;
import com.procureiq.springboot_app.shared.types.ApiResponse;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.RESOURCES)
@CrossOrigin(origins = "*")
public class ServiceResourceController {

    private final ServiceResourceService serviceResourceService;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public ServiceResourceController(ServiceResourceService serviceResourceService) {
        this.serviceResourceService = serviceResourceService;
    }

    @GetMapping
    public ResponseEntity<?> getAllServiceResources() {
        Span span = tracer.spanBuilder("REST.getAllServiceResources").startSpan();
        try (Scope scope = span.makeCurrent()) {
            List<ServiceResourceResponse> response = serviceResourceService.getAllServiceResources();
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
    public ResponseEntity<?> createServiceResource(@RequestBody ServiceResourceRequest request) {
        Span span = tracer.spanBuilder("REST.createServiceResource").startSpan();
        try (Scope scope = span.makeCurrent()) {
            ServiceResourceResponse response = serviceResourceService.createServiceResource(request);
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
    public ResponseEntity<?> getServiceResource(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.getServiceResource").startSpan();
        try (Scope scope = span.makeCurrent()) {
            ServiceResourceResponse response = serviceResourceService.getServiceResource(id);
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
    public ResponseEntity<?> updateServiceResource(@PathVariable Long id, @RequestBody ServiceResourceRequest request) {
        Span span = tracer.spanBuilder("REST.updateServiceResource").startSpan();
        try (Scope scope = span.makeCurrent()) {
            ServiceResourceResponse response = serviceResourceService.updateServiceResource(id, request);
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
    public ResponseEntity<?> deleteServiceResource(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.deleteServiceResource").startSpan();
        try (Scope scope = span.makeCurrent()) {
            serviceResourceService.deleteServiceResource(id);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, "Deleted resource successfully"));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }
}
