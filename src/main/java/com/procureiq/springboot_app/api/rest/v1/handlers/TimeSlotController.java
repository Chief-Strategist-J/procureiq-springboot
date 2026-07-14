package com.procureiq.springboot_app.api.rest.v1.handlers;

import com.procureiq.springboot_app.features.fieldservice.dto.TimeSlotRequest;
import com.procureiq.springboot_app.features.fieldservice.dto.TimeSlotResponse;
import com.procureiq.springboot_app.features.fieldservice.service.TimeSlotService;
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
@RequestMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.TIME_SLOTS)
@CrossOrigin(origins = "*")
public class TimeSlotController {

    private final TimeSlotService timeSlotService;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public TimeSlotController(TimeSlotService timeSlotService) {
        this.timeSlotService = timeSlotService;
    }

    @PostMapping
    public ResponseEntity<?> createTimeSlot(@RequestBody TimeSlotRequest request) {
        Span span = tracer.spanBuilder("REST.createTimeSlot").startSpan();
        try (Scope scope = span.makeCurrent()) {
            TimeSlotResponse response = timeSlotService.createTimeSlot(request);
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
    public ResponseEntity<?> getTimeSlot(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.getTimeSlot").startSpan();
        try (Scope scope = span.makeCurrent()) {
            TimeSlotResponse response = timeSlotService.getTimeSlot(id);
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
    public ResponseEntity<?> updateTimeSlot(@PathVariable Long id, @RequestBody TimeSlotRequest request) {
        Span span = tracer.spanBuilder("REST.updateTimeSlot").startSpan();
        try (Scope scope = span.makeCurrent()) {
            TimeSlotResponse response = timeSlotService.updateTimeSlot(id, request);
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
    public ResponseEntity<?> deleteTimeSlot(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.deleteTimeSlot").startSpan();
        try (Scope scope = span.makeCurrent()) {
            timeSlotService.deleteTimeSlot(id);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, "Deleted time slot successfully"));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }
}
