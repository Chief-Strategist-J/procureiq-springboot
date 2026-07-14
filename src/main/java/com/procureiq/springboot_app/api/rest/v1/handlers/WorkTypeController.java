package com.procureiq.springboot_app.api.rest.v1.handlers;

import com.procureiq.springboot_app.features.fieldservice.dto.request.WorkTypeRequest;
import com.procureiq.springboot_app.features.fieldservice.dto.response.WorkTypeResponse;
import com.procureiq.springboot_app.features.fieldservice.service.WorkTypeService;
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
@RequestMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.WORK_TYPES)
@CrossOrigin(origins = "*")
public class WorkTypeController {

    private final WorkTypeService workTypeService;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public WorkTypeController(WorkTypeService workTypeService) {
        this.workTypeService = workTypeService;
    }

    @PostMapping
    public ResponseEntity<?> createWorkType(@jakarta.validation.Valid @RequestBody WorkTypeRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            WorkTypeResponse response = workTypeService.createWorkType(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiSingleResponse.success(201, response));
        });
    }

    @GetMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> getWorkType(@PathVariable Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            WorkTypeResponse response = workTypeService.getWorkType(id);
            return ResponseEntity.ok(ApiSingleResponse.success(200, response));
        });
    }

    @PutMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> updateWorkType(@PathVariable Long id, @jakarta.validation.Valid @RequestBody WorkTypeRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            WorkTypeResponse response = workTypeService.updateWorkType(id, request);
            return ResponseEntity.ok(ApiSingleResponse.success(200, response));
        });
    }

    @DeleteMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> deleteWorkType(@PathVariable Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            workTypeService.deleteWorkType(id);
            return ResponseEntity.ok(ApiSingleResponse.success(200, "Deleted work type successfully"));
        });
    }
}
