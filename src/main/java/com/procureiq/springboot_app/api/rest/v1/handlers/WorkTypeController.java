package com.procureiq.springboot_app.api.rest.v1.handlers;

import com.procureiq.springboot_app.features.fieldservice.dto.WorkTypeRequest;
import com.procureiq.springboot_app.features.fieldservice.dto.WorkTypeResponse;
import com.procureiq.springboot_app.features.fieldservice.service.WorkTypeService;
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
@RequestMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.WORK_TYPES)
@CrossOrigin(origins = "*")
public class WorkTypeController {

    private final WorkTypeService workTypeService;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public WorkTypeController(WorkTypeService workTypeService) {
        this.workTypeService = workTypeService;
    }

    @PostMapping
    public ResponseEntity<?> createWorkType(@RequestBody WorkTypeRequest request) {
        Span span = tracer.spanBuilder("REST.createWorkType").startSpan();
        try (Scope scope = span.makeCurrent()) {
            WorkTypeResponse response = workTypeService.createWorkType(request);
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
    public ResponseEntity<?> getWorkType(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.getWorkType").startSpan();
        try (Scope scope = span.makeCurrent()) {
            WorkTypeResponse response = workTypeService.getWorkType(id);
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
    public ResponseEntity<?> updateWorkType(@PathVariable Long id, @RequestBody WorkTypeRequest request) {
        Span span = tracer.spanBuilder("REST.updateWorkType").startSpan();
        try (Scope scope = span.makeCurrent()) {
            WorkTypeResponse response = workTypeService.updateWorkType(id, request);
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
    public ResponseEntity<?> deleteWorkType(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.deleteWorkType").startSpan();
        try (Scope scope = span.makeCurrent()) {
            workTypeService.deleteWorkType(id);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, "Deleted work type successfully"));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }
}
