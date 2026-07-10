package com.procureiq.springboot_app.api.rest.v1.handlers;

import com.procureiq.springboot_app.features.fieldservice.dto.OperatingHoursRequest;
import com.procureiq.springboot_app.features.fieldservice.dto.OperatingHoursResponse;
import com.procureiq.springboot_app.features.fieldservice.service.OperatingHoursService;
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
@RequestMapping("/api/v1/fieldservice/operating-hours")
@CrossOrigin(origins = "*")
public class OperatingHoursController {

    private final OperatingHoursService operatingHoursService;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public OperatingHoursController(OperatingHoursService operatingHoursService) {
        this.operatingHoursService = operatingHoursService;
    }

    @PostMapping
    public ResponseEntity<?> createOperatingHours(@RequestBody OperatingHoursRequest request) {
        Span span = tracer.spanBuilder("REST.createOperatingHours").startSpan();
        try (Scope scope = span.makeCurrent()) {
            OperatingHoursResponse response = operatingHoursService.createOperatingHours(request);
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
    public ResponseEntity<?> getOperatingHours(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.getOperatingHours").startSpan();
        try (Scope scope = span.makeCurrent()) {
            OperatingHoursResponse response = operatingHoursService.getOperatingHours(id);
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
    public ResponseEntity<?> updateOperatingHours(@PathVariable Long id, @RequestBody OperatingHoursRequest request) {
        Span span = tracer.spanBuilder("REST.updateOperatingHours").startSpan();
        try (Scope scope = span.makeCurrent()) {
            OperatingHoursResponse response = operatingHoursService.updateOperatingHours(id, request);
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
    public ResponseEntity<?> deleteOperatingHours(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.deleteOperatingHours").startSpan();
        try (Scope scope = span.makeCurrent()) {
            operatingHoursService.deleteOperatingHours(id);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, "Deleted operating hours successfully"));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }
}
