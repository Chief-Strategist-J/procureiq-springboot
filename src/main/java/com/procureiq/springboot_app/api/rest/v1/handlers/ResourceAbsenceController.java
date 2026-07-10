package com.procureiq.springboot_app.api.rest.v1.handlers;

import com.procureiq.springboot_app.features.fieldservice.dto.ResourceAbsenceRequest;
import com.procureiq.springboot_app.features.fieldservice.dto.ResourceAbsenceResponse;
import com.procureiq.springboot_app.features.fieldservice.service.ResourceAbsenceService;
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
@RequestMapping("/api/v1/fieldservice/resource-absences")
@CrossOrigin(origins = "*")
public class ResourceAbsenceController {

    private final ResourceAbsenceService resourceAbsenceService;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public ResourceAbsenceController(ResourceAbsenceService resourceAbsenceService) {
        this.resourceAbsenceService = resourceAbsenceService;
    }

    @PostMapping
    public ResponseEntity<?> createResourceAbsence(@RequestBody ResourceAbsenceRequest request) {
        Span span = tracer.spanBuilder("REST.createResourceAbsence").startSpan();
        try (Scope scope = span.makeCurrent()) {
            ResourceAbsenceResponse response = resourceAbsenceService.createResourceAbsence(request);
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
    public ResponseEntity<?> getResourceAbsence(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.getResourceAbsence").startSpan();
        try (Scope scope = span.makeCurrent()) {
            ResourceAbsenceResponse response = resourceAbsenceService.getResourceAbsence(id);
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
    public ResponseEntity<?> updateResourceAbsence(@PathVariable Long id, @RequestBody ResourceAbsenceRequest request) {
        Span span = tracer.spanBuilder("REST.updateResourceAbsence").startSpan();
        try (Scope scope = span.makeCurrent()) {
            ResourceAbsenceResponse response = resourceAbsenceService.updateResourceAbsence(id, request);
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
    public ResponseEntity<?> deleteResourceAbsence(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.deleteResourceAbsence").startSpan();
        try (Scope scope = span.makeCurrent()) {
            resourceAbsenceService.deleteResourceAbsence(id);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, "Deleted resource absence successfully"));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }
}
