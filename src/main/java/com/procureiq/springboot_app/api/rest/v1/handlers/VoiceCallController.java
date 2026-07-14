package com.procureiq.springboot_app.api.rest.v1.handlers;

import com.procureiq.springboot_app.features.voice.entity.ScheduledCall;
import com.procureiq.springboot_app.features.voice.repository.ScheduledCallRepository;
import com.procureiq.springboot_app.shared.types.ApiResponse;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * VoiceCallController — REST handler for scheduled voice call management.
 *
 * Endpoints:
 *   POST   /api/v1/voice/schedule   — schedule a new voice call
 *   GET    /api/v1/voice/scheduled  — list all scheduled calls
 *   DELETE /api/v1/voice/{id}       — cancel / delete a scheduled call
 */
@RestController
@RequestMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.VOICE)
@CrossOrigin(origins = "*")
public class VoiceCallController {

    private final ScheduledCallRepository scheduledCallRepository;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public VoiceCallController(ScheduledCallRepository scheduledCallRepository) {
        this.scheduledCallRepository = scheduledCallRepository;
    }

    /**
     * POST /api/v1/voice/schedule
     * Schedule a new outbound voice call.
     *
     * Request body (JSON):
     *   phoneNumber   – E.164 destination number   (required)
     *   instructions  – spoken text for the call   (required)
     *   scheduledAt   – ISO-8601 timestamp          (required)
     *   provider      – "mock" | "twilio" | "vapi"  (optional, defaults to "mock")
     */
    @PostMapping("/schedule")
    public ResponseEntity<?> scheduleCall(@RequestBody Map<String, Object> body) {
        Span span = tracer.spanBuilder("REST.scheduleVoiceCall").startSpan();
        try (Scope scope = span.makeCurrent()) {

            String phoneNumber  = (String) body.get("phoneNumber");
            String instructions = (String) body.get("instructions");
            String scheduledAtRaw = body.get("scheduledAt") != null
                ? body.get("scheduledAt").toString() : null;
            String provider = body.get("provider") != null
                ? body.get("provider").toString() : "mock";

            if (phoneNumber == null || phoneNumber.isBlank()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "phoneNumber is required"));
            }
            if (instructions == null || instructions.isBlank()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "instructions is required"));
            }
            if (scheduledAtRaw == null || scheduledAtRaw.isBlank()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "scheduledAt is required"));
            }

            Instant scheduledAt;
            try {
                scheduledAt = Instant.parse(scheduledAtRaw);
            } catch (Exception e) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "scheduledAt must be a valid ISO-8601 timestamp"));
            }

            ScheduledCall call = new ScheduledCall();
            call.setPhoneNumber(phoneNumber);
            call.setInstructions(instructions);
            call.setScheduledAt(scheduledAt);
            call.setProvider(provider);
            call.setStatus("PENDING");
            call.setCreatedAt(Instant.now());

            ScheduledCall saved = scheduledCallRepository.save(call);

            span.setStatus(StatusCode.OK);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, saved));
        } catch (Exception e) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(500, e.getMessage()));
        } finally {
            span.end();
        }
    }

    /**
     * GET /api/v1/voice/scheduled
     * List all scheduled voice calls regardless of status.
     */
    @GetMapping("/scheduled")
    public ResponseEntity<?> listScheduledCalls() {
        Span span = tracer.spanBuilder("REST.listScheduledCalls").startSpan();
        try (Scope scope = span.makeCurrent()) {
            List<ScheduledCall> calls = scheduledCallRepository.findAll();
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, calls));
        } catch (Exception e) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(500, e.getMessage()));
        } finally {
            span.end();
        }
    }

    /**
     * DELETE /api/v1/voice/{id}
     * Delete / cancel a scheduled voice call by ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteScheduledCall(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.deleteScheduledCall").startSpan();
        try (Scope scope = span.makeCurrent()) {
            if (!scheduledCallRepository.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "Scheduled call not found with ID: " + id));
            }
            scheduledCallRepository.deleteById(id);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, "Scheduled call deleted successfully"));
        } catch (Exception e) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(500, e.getMessage()));
        } finally {
            span.end();
        }
    }
}
