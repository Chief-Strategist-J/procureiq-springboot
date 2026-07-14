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
        Span span = tracer.spanBuilder("REST.createCaseMilestone").startSpan();
        try (Scope scope = span.makeCurrent()) {
            CaseMilestoneResponse response = caseMilestoneService.createCaseMilestone(request);
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
    public ResponseEntity<?> getCaseMilestone(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.getCaseMilestone").startSpan();
        try (Scope scope = span.makeCurrent()) {
            CaseMilestoneResponse response = caseMilestoneService.getCaseMilestone(id);
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
    public ResponseEntity<?> updateCaseMilestone(@PathVariable Long id, @RequestBody CaseMilestoneRequest request) {
        Span span = tracer.spanBuilder("REST.updateCaseMilestone").startSpan();
        try (Scope scope = span.makeCurrent()) {
            CaseMilestoneResponse response = caseMilestoneService.updateCaseMilestone(id, request);
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
    public ResponseEntity<?> deleteCaseMilestone(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.deleteCaseMilestone").startSpan();
        try (Scope scope = span.makeCurrent()) {
            caseMilestoneService.deleteCaseMilestone(id);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, "Deleted case milestone successfully"));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }
}
