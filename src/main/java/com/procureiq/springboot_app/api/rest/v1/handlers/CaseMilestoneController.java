package com.procureiq.springboot_app.api.rest.v1.handlers;

import com.procureiq.springboot_app.features.fieldservice.dto.CaseMilestoneRequest;
import com.procureiq.springboot_app.features.fieldservice.dto.CaseMilestoneResponse;
import com.procureiq.springboot_app.features.fieldservice.service.CaseMilestoneService;
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
@RequestMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.CASE_MILESTONES)
@CrossOrigin(origins = "*")
public class CaseMilestoneController {

    private final CaseMilestoneService caseMilestoneService;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public CaseMilestoneController(CaseMilestoneService caseMilestoneService) {
        this.caseMilestoneService = caseMilestoneService;
    }

    @PostMapping
    public ResponseEntity<?> createCaseMilestone(@RequestBody CaseMilestoneRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            CaseMilestoneResponse response = caseMilestoneService.createCaseMilestone(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(201, response));
        });
    }

    @GetMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> getCaseMilestone(@PathVariable Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            CaseMilestoneResponse response = caseMilestoneService.getCaseMilestone(id);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        });
    }

    @PutMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> updateCaseMilestone(@PathVariable Long id, @RequestBody CaseMilestoneRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            CaseMilestoneResponse response = caseMilestoneService.updateCaseMilestone(id, request);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        });
    }

    @DeleteMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> deleteCaseMilestone(@PathVariable Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            caseMilestoneService.deleteCaseMilestone(id);
            return ResponseEntity.ok(ApiResponse.success(200, "Deleted case milestone successfully"));
        });
    }
}
