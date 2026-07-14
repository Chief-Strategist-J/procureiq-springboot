package com.procureiq.springboot_app.api.rest.v1.handlers;

import com.procureiq.springboot_app.features.fieldservice.dto.ServiceCrewMemberRequest;
import com.procureiq.springboot_app.features.fieldservice.dto.ServiceCrewMemberResponse;
import com.procureiq.springboot_app.features.fieldservice.service.ServiceCrewMemberService;
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
@RequestMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.SERVICE_CREW_MEMBERS)
@CrossOrigin(origins = "*")
public class ServiceCrewMemberController {

    private final ServiceCrewMemberService serviceCrewMemberService;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public ServiceCrewMemberController(ServiceCrewMemberService serviceCrewMemberService) {
        this.serviceCrewMemberService = serviceCrewMemberService;
    }

    @PostMapping
    public ResponseEntity<?> createServiceCrewMember(@RequestBody ServiceCrewMemberRequest request) {
        Span span = tracer.spanBuilder("REST.createServiceCrewMember").startSpan();
        try (Scope scope = span.makeCurrent()) {
            ServiceCrewMemberResponse response = serviceCrewMemberService.createServiceCrewMember(request);
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
    public ResponseEntity<?> getServiceCrewMember(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.getServiceCrewMember").startSpan();
        try (Scope scope = span.makeCurrent()) {
            ServiceCrewMemberResponse response = serviceCrewMemberService.getServiceCrewMember(id);
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
    public ResponseEntity<?> updateServiceCrewMember(@PathVariable Long id, @RequestBody ServiceCrewMemberRequest request) {
        Span span = tracer.spanBuilder("REST.updateServiceCrewMember").startSpan();
        try (Scope scope = span.makeCurrent()) {
            ServiceCrewMemberResponse response = serviceCrewMemberService.updateServiceCrewMember(id, request);
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
    public ResponseEntity<?> deleteServiceCrewMember(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.deleteServiceCrewMember").startSpan();
        try (Scope scope = span.makeCurrent()) {
            serviceCrewMemberService.deleteServiceCrewMember(id);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, "Deleted service crew member successfully"));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }
}
