package com.procureiq.springboot_app.api.rest.v1.handlers;

import com.procureiq.springboot_app.features.fieldservice.dto.request.OperatingHoursRequest;
import com.procureiq.springboot_app.features.fieldservice.dto.response.OperatingHoursResponse;
import com.procureiq.springboot_app.features.fieldservice.service.OperatingHoursService;
import com.procureiq.springboot_app.shared.types.single.ApiSingleResponse;
import com.procureiq.springboot_app.shared.types.list.ApiListResponse;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.OPERATING_HOURS)
public class OperatingHoursController {
    private final OperatingHoursService operatingHoursService;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public OperatingHoursController(OperatingHoursService operatingHoursService) {
        this.operatingHoursService = operatingHoursService;
    }

    @GetMapping
    public ResponseEntity<?> getAllOperatingHours() {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            java.util.List<OperatingHoursResponse> response = operatingHoursService.getAllOperatingHours();
            return ResponseEntity.ok(ApiListResponse.success(200, response));
        });
    }

    @PostMapping
    public ResponseEntity<?> createOperatingHours(@jakarta.validation.Valid @RequestBody OperatingHoursRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            OperatingHoursResponse response = operatingHoursService.createOperatingHours(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiSingleResponse.success(201, response));
        });
    }

    @GetMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> getOperatingHours(@PathVariable Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            OperatingHoursResponse response = operatingHoursService.getOperatingHours(id);
            return ResponseEntity.ok(ApiSingleResponse.success(200, response));
        });
    }

    @PutMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> updateOperatingHours(@PathVariable Long id, @jakarta.validation.Valid @RequestBody OperatingHoursRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            OperatingHoursResponse response = operatingHoursService.updateOperatingHours(id, request);
            return ResponseEntity.ok(ApiSingleResponse.success(200, response));
        });
    }

    @DeleteMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> deleteOperatingHours(@PathVariable Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            operatingHoursService.deleteOperatingHours(id);
            return ResponseEntity.ok(ApiSingleResponse.success(200, "Deleted operating hours successfully"));
        });
    }
}
