package com.procureiq.springboot_app.api.rest.v1.handlers;

import com.procureiq.springboot_app.features.fieldservice.dto.*;
import com.procureiq.springboot_app.features.fieldservice.service.ServiceAppointmentService;
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
@RequestMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.SERVICE_APPOINTMENTS)
@CrossOrigin(origins = "*")
public class ServiceAppointmentController {

    private final ServiceAppointmentService serviceAppointmentService;
    private final ServiceResourceService serviceResourceService;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public ServiceAppointmentController(
            ServiceAppointmentService serviceAppointmentService,
            ServiceResourceService serviceResourceService) {
        this.serviceAppointmentService = serviceAppointmentService;
        this.serviceResourceService = serviceResourceService;
    }

    @GetMapping("/appointments")
    public ResponseEntity<?> getAllServiceAppointments() {
        Span span = tracer.spanBuilder("REST.getAllServiceAppointments").startSpan();
        try (Scope scope = span.makeCurrent()) {
            List<ServiceAppointmentResponse> response = serviceAppointmentService.getAllServiceAppointments();
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(500, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @PostMapping("/appointments")
    public ResponseEntity<?> createServiceAppointment(@RequestBody ServiceAppointmentRequest request) {
        Span span = tracer.spanBuilder("REST.createServiceAppointment").startSpan();
        try (Scope scope = span.makeCurrent()) {
            ServiceAppointmentResponse response = serviceAppointmentService.createServiceAppointment(request);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(201, response));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @GetMapping("/appointments/{id}")
    public ResponseEntity<?> getServiceAppointment(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.getServiceAppointment").startSpan();
        try (Scope scope = span.makeCurrent()) {
            ServiceAppointmentResponse response = serviceAppointmentService.getServiceAppointment(id);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @PutMapping("/appointments/{id}")
    public ResponseEntity<?> updateServiceAppointment(@PathVariable Long id, @RequestBody ServiceAppointmentRequest request) {
        Span span = tracer.spanBuilder("REST.updateServiceAppointment").startSpan();
        try (Scope scope = span.makeCurrent()) {
            ServiceAppointmentResponse response = serviceAppointmentService.updateServiceAppointment(id, request);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @DeleteMapping("/appointments/{id}")
    public ResponseEntity<?> deleteServiceAppointment(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.deleteServiceAppointment").startSpan();
        try (Scope scope = span.makeCurrent()) {
            serviceAppointmentService.deleteServiceAppointment(id);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, "Deleted appointment successfully"));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @PostMapping("/appointments/{appointmentId}/assign")
    public ResponseEntity<?> assignResource(
            @PathVariable Long appointmentId,
            @RequestBody AssignResourceRequest request) {
        Span span = tracer.spanBuilder("REST.assignResource").startSpan();
        try (Scope scope = span.makeCurrent()) {
            AssignedResourceResponse response = serviceAppointmentService.assignResource(appointmentId, request);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(201, response));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @DeleteMapping("/assigned-resources/{id}")
    public ResponseEntity<?> deleteAssignedResource(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.deleteAssignedResource").startSpan();
        try (Scope scope = span.makeCurrent()) {
            serviceAppointmentService.deleteAssignedResource(id);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, "Deleted assignment successfully"));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @GetMapping("/appointments/{appointmentId}/candidates")
    public ResponseEntity<?> getCandidates(@PathVariable Long appointmentId) {
        Span span = tracer.spanBuilder("REST.getCandidates").startSpan();
        try (Scope scope = span.makeCurrent()) {
            List<ServiceResourceResponse> response = serviceResourceService.getCandidatesForAppointment(appointmentId);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }
}
