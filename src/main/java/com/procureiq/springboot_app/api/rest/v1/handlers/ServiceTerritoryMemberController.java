package com.procureiq.springboot_app.api.rest.v1.handlers;

import com.procureiq.springboot_app.features.fieldservice.dto.ServiceTerritoryMemberRequest;
import com.procureiq.springboot_app.features.fieldservice.dto.ServiceTerritoryMemberResponse;
import com.procureiq.springboot_app.features.fieldservice.service.ServiceTerritoryMemberService;
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
@RequestMapping("/api/v1/fieldservice/service-territory-members")
@CrossOrigin(origins = "*")
public class ServiceTerritoryMemberController {

    private final ServiceTerritoryMemberService serviceTerritoryMemberService;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public ServiceTerritoryMemberController(ServiceTerritoryMemberService serviceTerritoryMemberService) {
        this.serviceTerritoryMemberService = serviceTerritoryMemberService;
    }

    @PostMapping
    public ResponseEntity<?> createServiceTerritoryMember(@RequestBody ServiceTerritoryMemberRequest request) {
        Span span = tracer.spanBuilder("REST.createServiceTerritoryMember").startSpan();
        try (Scope scope = span.makeCurrent()) {
            ServiceTerritoryMemberResponse response = serviceTerritoryMemberService.createServiceTerritoryMember(request);
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
    public ResponseEntity<?> getServiceTerritoryMember(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.getServiceTerritoryMember").startSpan();
        try (Scope scope = span.makeCurrent()) {
            ServiceTerritoryMemberResponse response = serviceTerritoryMemberService.getServiceTerritoryMember(id);
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
    public ResponseEntity<?> updateServiceTerritoryMember(@PathVariable Long id, @RequestBody ServiceTerritoryMemberRequest request) {
        Span span = tracer.spanBuilder("REST.updateServiceTerritoryMember").startSpan();
        try (Scope scope = span.makeCurrent()) {
            ServiceTerritoryMemberResponse response = serviceTerritoryMemberService.updateServiceTerritoryMember(id, request);
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
    public ResponseEntity<?> deleteServiceTerritoryMember(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.deleteServiceTerritoryMember").startSpan();
        try (Scope scope = span.makeCurrent()) {
            serviceTerritoryMemberService.deleteServiceTerritoryMember(id);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, "Deleted service territory member successfully"));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }
}
