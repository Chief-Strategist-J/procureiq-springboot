package com.procureiq.springboot_app.api.rest.v1.handlers;

import com.procureiq.springboot_app.features.fieldservice.dto.WorkOrderRequest;
import com.procureiq.springboot_app.features.fieldservice.dto.WorkOrderResponse;
import com.procureiq.springboot_app.features.fieldservice.service.WorkOrderService;
import com.procureiq.springboot_app.shared.types.ApiResponse;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.WORK_ORDERS)
@CrossOrigin(origins = "*")
public class WorkOrderController {

    private final WorkOrderService workOrderService;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public WorkOrderController(WorkOrderService workOrderService) {
        this.workOrderService = workOrderService;
    }

    @GetMapping
    public ResponseEntity<?> getAllWorkOrders() {
        Span span = tracer.spanBuilder("REST.getAllWorkOrders").startSpan();
        try (Scope scope = span.makeCurrent()) {
            List<WorkOrderResponse> response = workOrderService.getAllWorkOrders();
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(500, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @PostMapping
    public ResponseEntity<?> createWorkOrder(@RequestBody WorkOrderRequest request) {
        Span span = tracer.spanBuilder("REST.createWorkOrder").startSpan();
        try (Scope scope = span.makeCurrent()) {
            WorkOrderResponse response = workOrderService.createWorkOrder(request);
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
    public ResponseEntity<?> getWorkOrder(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.getWorkOrder").startSpan();
        try (Scope scope = span.makeCurrent()) {
            WorkOrderResponse response = workOrderService.getWorkOrder(id);
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
    public ResponseEntity<?> updateWorkOrder(@PathVariable Long id, @RequestBody WorkOrderRequest request) {
        Span span = tracer.spanBuilder("REST.updateWorkOrder").startSpan();
        try (Scope scope = span.makeCurrent()) {
            WorkOrderResponse response = workOrderService.updateWorkOrder(id, request);
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
    public ResponseEntity<?> deleteWorkOrder(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.deleteWorkOrder").startSpan();
        try (Scope scope = span.makeCurrent()) {
            workOrderService.deleteWorkOrder(id);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, "Deleted work order successfully"));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }
}
