package com.procureiq.springboot_app.api.rest.v1.handlers;

import com.procureiq.springboot_app.features.fieldservice.dto.ServiceResourceSkillRequest;
import com.procureiq.springboot_app.features.fieldservice.dto.ServiceResourceSkillResponse;
import com.procureiq.springboot_app.features.fieldservice.service.ServiceResourceSkillService;
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
@RequestMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.SERVICE_RESOURCE_SKILLS)
@CrossOrigin(origins = "*")
public class ServiceResourceSkillController {

    private final ServiceResourceSkillService serviceResourceSkillService;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public ServiceResourceSkillController(ServiceResourceSkillService serviceResourceSkillService) {
        this.serviceResourceSkillService = serviceResourceSkillService;
    }

    @PostMapping
    public ResponseEntity<?> createServiceResourceSkill(@RequestBody ServiceResourceSkillRequest request) {
        Span span = tracer.spanBuilder("REST.createServiceResourceSkill").startSpan();
        try (Scope scope = span.makeCurrent()) {
            ServiceResourceSkillResponse response = serviceResourceSkillService.createServiceResourceSkill(request);
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
    public ResponseEntity<?> getServiceResourceSkill(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.getServiceResourceSkill").startSpan();
        try (Scope scope = span.makeCurrent()) {
            ServiceResourceSkillResponse response = serviceResourceSkillService.getServiceResourceSkill(id);
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
    public ResponseEntity<?> updateServiceResourceSkill(@PathVariable Long id, @RequestBody ServiceResourceSkillRequest request) {
        Span span = tracer.spanBuilder("REST.updateServiceResourceSkill").startSpan();
        try (Scope scope = span.makeCurrent()) {
            ServiceResourceSkillResponse response = serviceResourceSkillService.updateServiceResourceSkill(id, request);
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
    public ResponseEntity<?> deleteServiceResourceSkill(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.deleteServiceResourceSkill").startSpan();
        try (Scope scope = span.makeCurrent()) {
            serviceResourceSkillService.deleteServiceResourceSkill(id);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, "Deleted service resource skill successfully"));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }
}
