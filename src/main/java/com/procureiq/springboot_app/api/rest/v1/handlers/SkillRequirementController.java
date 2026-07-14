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
    public ResponseEntity<?> createSkillRequirement(@RequestBody SkillRequirementRequest request) {
        Span span = tracer.spanBuilder("REST.createSkillRequirement").startSpan();
        try (Scope scope = span.makeCurrent()) {
            SkillRequirementResponse response = skillRequirementService.createSkillRequirement(request);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(201, response));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @GetMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> getSkillRequirement(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.getSkillRequirement").startSpan();
        try (Scope scope = span.makeCurrent()) {
            SkillRequirementResponse response = skillRequirementService.getSkillRequirement(id);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @PutMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> updateSkillRequirement(@PathVariable Long id, @RequestBody SkillRequirementRequest request) {
        Span span = tracer.spanBuilder("REST.updateSkillRequirement").startSpan();
        try (Scope scope = span.makeCurrent()) {
            SkillRequirementResponse response = skillRequirementService.updateSkillRequirement(id, request);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @DeleteMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> deleteSkillRequirement(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.deleteSkillRequirement").startSpan();
        try (Scope scope = span.makeCurrent()) {
            skillRequirementService.deleteSkillRequirement(id);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, "Deleted skill requirement successfully"));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }
}
