package com.procureiq.springboot_app.api.rest.v1.handlers;

import com.procureiq.springboot_app.features.fieldservice.dto.request.ServiceTerritoryMemberRequest;
import com.procureiq.springboot_app.features.fieldservice.dto.response.ServiceTerritoryMemberResponse;
import com.procureiq.springboot_app.features.fieldservice.service.ServiceTerritoryMemberService;
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
@RequestMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.SERVICE_TERRITORY_MEMBERS)
@CrossOrigin(origins = "*")
public class ServiceTerritoryMemberController {

    private final ServiceTerritoryMemberService serviceTerritoryMemberService;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public ServiceTerritoryMemberController(ServiceTerritoryMemberService serviceTerritoryMemberService) {
        this.serviceTerritoryMemberService = serviceTerritoryMemberService;
    }

    @PostMapping
    public ResponseEntity<?> createServiceTerritoryMember(@jakarta.validation.Valid @RequestBody ServiceTerritoryMemberRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            ServiceTerritoryMemberResponse response = serviceTerritoryMemberService.createServiceTerritoryMember(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiSingleResponse.success(201, response));
        });
    }

    @GetMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> getServiceTerritoryMember(@PathVariable Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            ServiceTerritoryMemberResponse response = serviceTerritoryMemberService.getServiceTerritoryMember(id);
            return ResponseEntity.ok(ApiSingleResponse.success(200, response));
        });
    }

    @PutMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> updateServiceTerritoryMember(@PathVariable Long id, @jakarta.validation.Valid @RequestBody ServiceTerritoryMemberRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            ServiceTerritoryMemberResponse response = serviceTerritoryMemberService.updateServiceTerritoryMember(id, request);
            return ResponseEntity.ok(ApiSingleResponse.success(200, response));
        });
    }

    @DeleteMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> deleteServiceTerritoryMember(@PathVariable Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            serviceTerritoryMemberService.deleteServiceTerritoryMember(id);
            return ResponseEntity.ok(ApiSingleResponse.success(200, "Deleted service territory member successfully"));
        });
    }
}
