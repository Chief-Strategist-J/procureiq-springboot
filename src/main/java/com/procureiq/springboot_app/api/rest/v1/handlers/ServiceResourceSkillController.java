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
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            ServiceResourceSkillResponse response = serviceResourceSkillService.createServiceResourceSkill(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(201, response));
        });
    }

    @GetMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> getServiceResourceSkill(@PathVariable Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            ServiceResourceSkillResponse response = serviceResourceSkillService.getServiceResourceSkill(id);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        });
    }

    @PutMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> updateServiceResourceSkill(@PathVariable Long id, @RequestBody ServiceResourceSkillRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            ServiceResourceSkillResponse response = serviceResourceSkillService.updateServiceResourceSkill(id, request);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        });
    }

    @DeleteMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> deleteServiceResourceSkill(@PathVariable Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            serviceResourceSkillService.deleteServiceResourceSkill(id);
            return ResponseEntity.ok(ApiResponse.success(200, "Deleted service resource skill successfully"));
        });
    }
}
