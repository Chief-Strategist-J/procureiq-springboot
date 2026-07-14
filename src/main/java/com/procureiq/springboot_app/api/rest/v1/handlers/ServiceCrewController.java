package com.procureiq.springboot_app.api.rest.v1.handlers;

import com.procureiq.springboot_app.features.fieldservice.dto.request.ServiceCrewRequest;
import com.procureiq.springboot_app.features.fieldservice.dto.response.ServiceCrewResponse;
import com.procureiq.springboot_app.features.fieldservice.service.ServiceCrewService;
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

@RestController
@RequestMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.SERVICE_CREWS)
@CrossOrigin(origins = "*")
public class ServiceCrewController {

    private final ServiceCrewService serviceCrewService;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public ServiceCrewController(ServiceCrewService serviceCrewService) {
        this.serviceCrewService = serviceCrewService;
    }

    @PostMapping
    public ResponseEntity<?> createServiceCrew(@jakarta.validation.Valid @RequestBody ServiceCrewRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            ServiceCrewResponse response = serviceCrewService.createServiceCrew(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiSingleResponse.success(201, response));
        });
    }

    @GetMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> getServiceCrew(@PathVariable Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            ServiceCrewResponse response = serviceCrewService.getServiceCrew(id);
            return ResponseEntity.ok(ApiSingleResponse.success(200, response));
        });
    }

    @PutMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> updateServiceCrew(@PathVariable Long id, @jakarta.validation.Valid @RequestBody ServiceCrewRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            ServiceCrewResponse response = serviceCrewService.updateServiceCrew(id, request);
            return ResponseEntity.ok(ApiSingleResponse.success(200, response));
        });
    }

    @DeleteMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> deleteServiceCrew(@PathVariable Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            serviceCrewService.deleteServiceCrew(id);
            return ResponseEntity.ok(ApiSingleResponse.success(200, "Deleted service crew successfully"));
        });
    }
}
