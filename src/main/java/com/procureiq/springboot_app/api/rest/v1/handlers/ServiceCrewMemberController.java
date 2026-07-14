package com.procureiq.springboot_app.api.rest.v1.handlers;

import com.procureiq.springboot_app.features.fieldservice.dto.request.ServiceCrewMemberRequest;
import com.procureiq.springboot_app.features.fieldservice.dto.response.ServiceCrewMemberResponse;
import com.procureiq.springboot_app.features.fieldservice.service.ServiceCrewMemberService;
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
@RequestMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.SERVICE_CREW_MEMBERS)
@CrossOrigin(origins = "*")
public class ServiceCrewMemberController {

    private final ServiceCrewMemberService serviceCrewMemberService;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public ServiceCrewMemberController(ServiceCrewMemberService serviceCrewMemberService) {
        this.serviceCrewMemberService = serviceCrewMemberService;
    }

    @PostMapping
    public ResponseEntity<?> createServiceCrewMember(@jakarta.validation.Valid @RequestBody ServiceCrewMemberRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            ServiceCrewMemberResponse response = serviceCrewMemberService.createServiceCrewMember(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiSingleResponse.success(201, response));
        });
    }

    @GetMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> getServiceCrewMember(@PathVariable Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            ServiceCrewMemberResponse response = serviceCrewMemberService.getServiceCrewMember(id);
            return ResponseEntity.ok(ApiSingleResponse.success(200, response));
        });
    }

    @PutMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> updateServiceCrewMember(@PathVariable Long id, @jakarta.validation.Valid @RequestBody ServiceCrewMemberRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            ServiceCrewMemberResponse response = serviceCrewMemberService.updateServiceCrewMember(id, request);
            return ResponseEntity.ok(ApiSingleResponse.success(200, response));
        });
    }

    @DeleteMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> deleteServiceCrewMember(@PathVariable Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            serviceCrewMemberService.deleteServiceCrewMember(id);
            return ResponseEntity.ok(ApiSingleResponse.success(200, "Deleted service crew member successfully"));
        });
    }
}
