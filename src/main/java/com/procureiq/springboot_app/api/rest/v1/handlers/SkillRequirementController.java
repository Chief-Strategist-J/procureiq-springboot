package com.procureiq.springboot_app.api.rest.v1.handlers;

import com.procureiq.springboot_app.features.fieldservice.dto.SkillRequirementRequest;
import com.procureiq.springboot_app.features.fieldservice.dto.SkillRequirementResponse;
import com.procureiq.springboot_app.features.fieldservice.service.SkillRequirementService;
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
@RequestMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.SKILL_REQUIREMENTS)
@CrossOrigin(origins = "*")
public class SkillRequirementController {

    private final SkillRequirementService skillRequirementService;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public SkillRequirementController(SkillRequirementService skillRequirementService) {
        this.skillRequirementService = skillRequirementService;
    }

    @PostMapping
    public ResponseEntity<?> createSkillRequirement(@jakarta.validation.Valid @RequestBody SkillRequirementRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            SkillRequirementResponse response = skillRequirementService.createSkillRequirement(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(201, response));
        });
    }

    @GetMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> getSkillRequirement(@PathVariable Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            SkillRequirementResponse response = skillRequirementService.getSkillRequirement(id);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        });
    }

    @PutMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> updateSkillRequirement(@PathVariable Long id, @jakarta.validation.Valid @RequestBody SkillRequirementRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            SkillRequirementResponse response = skillRequirementService.updateSkillRequirement(id, request);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        });
    }

    @DeleteMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> deleteSkillRequirement(@PathVariable Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            skillRequirementService.deleteSkillRequirement(id);
            return ResponseEntity.ok(ApiResponse.success(200, "Deleted skill requirement successfully"));
        });
    }
}
