package com.procureiq.springboot_app.api.rest.v1.handlers;

import com.procureiq.springboot_app.features.fieldservice.dto.ShiftRequest;
import com.procureiq.springboot_app.features.fieldservice.dto.ShiftResponse;
import com.procureiq.springboot_app.features.fieldservice.service.ShiftService;
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
@RequestMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.SHIFTS)
@CrossOrigin(origins = "*")
public class ShiftController {

    private final ShiftService shiftService;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public ShiftController(ShiftService shiftService) {
        this.shiftService = shiftService;
    }

    @PostMapping
    public ResponseEntity<?> createShift(@jakarta.validation.Valid @RequestBody ShiftRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            ShiftResponse response = shiftService.createShift(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(201, response));
        });
    }

    @GetMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> getShift(@PathVariable Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            ShiftResponse response = shiftService.getShift(id);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        });
    }

    @PutMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> updateShift(@PathVariable Long id, @jakarta.validation.Valid @RequestBody ShiftRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            ShiftResponse response = shiftService.updateShift(id, request);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        });
    }

    @DeleteMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> deleteShift(@PathVariable Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            shiftService.deleteShift(id);
            return ResponseEntity.ok(ApiResponse.success(200, "Deleted shift successfully"));
        });
    }
}
