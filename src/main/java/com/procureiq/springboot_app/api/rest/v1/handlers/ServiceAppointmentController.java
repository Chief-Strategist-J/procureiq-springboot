package com.procureiq.springboot_app.api.rest.v1.handlers;

import com.procureiq.springboot_app.features.fieldservice.dto.request.*;
import com.procureiq.springboot_app.features.fieldservice.dto.response.*;
import com.procureiq.springboot_app.features.fieldservice.service.ServiceAppointmentService;
import com.procureiq.springboot_app.features.fieldservice.service.ServiceResourceService;
import com.procureiq.springboot_app.shared.types.ApiSingleResponse;
import com.procureiq.springboot_app.shared.types.ApiListResponse;
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

    @GetMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.APPOINTMENTS_SUB)
    public ResponseEntity<?> getAllServiceAppointments() {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            List<ServiceAppointmentResponse> response = serviceAppointmentService.getAllServiceAppointments();
            return ResponseEntity.ok(ApiListResponse.success(200, response));
        });
    }

    @PostMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.APPOINTMENTS_SUB)
    public ResponseEntity<?> createServiceAppointment(@jakarta.validation.Valid @RequestBody ServiceAppointmentRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            ServiceAppointmentResponse response = serviceAppointmentService.createServiceAppointment(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiSingleResponse.success(201, response));
        });
    }

    @GetMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.APPOINTMENTS_ID)
    public ResponseEntity<?> getServiceAppointment(@PathVariable Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            ServiceAppointmentResponse response = serviceAppointmentService.getServiceAppointment(id);
            return ResponseEntity.ok(ApiSingleResponse.success(200, response));
        });
    }

    @PutMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.APPOINTMENTS_ID)
    public ResponseEntity<?> updateServiceAppointment(@PathVariable Long id, @jakarta.validation.Valid @RequestBody ServiceAppointmentRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            ServiceAppointmentResponse response = serviceAppointmentService.updateServiceAppointment(id, request);
            return ResponseEntity.ok(ApiSingleResponse.success(200, response));
        });
    }

    @DeleteMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.APPOINTMENTS_ID)
    public ResponseEntity<?> deleteServiceAppointment(@PathVariable Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            serviceAppointmentService.deleteServiceAppointment(id);
            return ResponseEntity.ok(ApiSingleResponse.success(200, "Deleted appointment successfully"));
        });
    }

    @PostMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.APPOINTMENTS_ASSIGN)
    public ResponseEntity<?> assignResource(
            @PathVariable Long appointmentId,
            @jakarta.validation.Valid @RequestBody AssignResourceRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            AssignedResourceResponse response = serviceAppointmentService.assignResource(appointmentId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiSingleResponse.success(201, response));
        });
    }

    @DeleteMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.ASSIGNED_RESOURCES_ID)
    public ResponseEntity<?> deleteAssignedResource(@PathVariable Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            serviceAppointmentService.deleteAssignedResource(id);
            return ResponseEntity.ok(ApiSingleResponse.success(200, "Deleted assignment successfully"));
        });
    }

    @GetMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.APPOINTMENTS_CANDIDATES)
    public ResponseEntity<?> getCandidates(@PathVariable Long appointmentId) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            List<ServiceResourceResponse> response = serviceResourceService.getCandidatesForAppointment(appointmentId);
            return ResponseEntity.ok(ApiListResponse.success(200, response));
        });
    }
}
