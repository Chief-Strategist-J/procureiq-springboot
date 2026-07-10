package com.procureiq.springboot_app.api.rest.v1.handlers;

import com.procureiq.springboot_app.features.fieldservice.dto.ServiceCrewRequest;
import com.procureiq.springboot_app.features.fieldservice.dto.ServiceCrewResponse;
import com.procureiq.springboot_app.features.fieldservice.service.ServiceCrewService;
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
@RequestMapping("/api/v1/fieldservice/service-crews")
@CrossOrigin(origins = "*")
public class ServiceCrewController {

    private final ServiceCrewService serviceCrewService;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public ServiceCrewController(ServiceCrewService serviceCrewService) {
        this.serviceCrewService = serviceCrewService;
    }

    @PostMapping
    public ResponseEntity<?> createServiceCrew(@RequestBody ServiceCrewRequest request) {
        Span span = tracer.spanBuilder("REST.createServiceCrew").startSpan();
        try (Scope scope = span.makeCurrent()) {
            ServiceCrewResponse response = serviceCrewService.createServiceCrew(request);
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
    public ResponseEntity<?> getServiceCrew(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.getServiceCrew").startSpan();
        try (Scope scope = span.makeCurrent()) {
            ServiceCrewResponse response = serviceCrewService.getServiceCrew(id);
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
    public ResponseEntity<?> updateServiceCrew(@PathVariable Long id, @RequestBody ServiceCrewRequest request) {
        Span span = tracer.spanBuilder("REST.updateServiceCrew").startSpan();
        try (Scope scope = span.makeCurrent()) {
            ServiceCrewResponse response = serviceCrewService.updateServiceCrew(id, request);
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
    public ResponseEntity<?> deleteServiceCrew(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.deleteServiceCrew").startSpan();
        try (Scope scope = span.makeCurrent()) {
            serviceCrewService.deleteServiceCrew(id);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, "Deleted service crew successfully"));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }
}
