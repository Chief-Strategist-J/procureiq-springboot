package com.procureiq.springboot_app.api.rest.v1.handlers;

import com.procureiq.springboot_app.features.fieldservice.dto.MilestoneRequest;
import com.procureiq.springboot_app.features.fieldservice.dto.MilestoneResponse;
import com.procureiq.springboot_app.features.fieldservice.service.MilestoneService;
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
@RequestMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.MILESTONES)
@CrossOrigin(origins = "*")
public class MilestoneController {

    private final MilestoneService milestoneService;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public MilestoneController(MilestoneService milestoneService) {
        this.milestoneService = milestoneService;
    }

    @PostMapping
    public ResponseEntity<?> createMilestone(@RequestBody MilestoneRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            MilestoneResponse response = milestoneService.createMilestone(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(201, response));
        });
    }

    @GetMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> getMilestone(@PathVariable Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            MilestoneResponse response = milestoneService.getMilestone(id);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        });
    }

    @PutMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> updateMilestone(@PathVariable Long id, @RequestBody MilestoneRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            MilestoneResponse response = milestoneService.updateMilestone(id, request);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        });
    }

    @DeleteMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> deleteMilestone(@PathVariable Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            milestoneService.deleteMilestone(id);
            return ResponseEntity.ok(ApiResponse.success(200, "Deleted milestone successfully"));
        });
    }
}
