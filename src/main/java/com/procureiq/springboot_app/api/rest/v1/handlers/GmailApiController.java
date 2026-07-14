package com.procureiq.springboot_app.api.rest.v1.handlers;

import com.google.api.services.gmail.model.Message;
import com.procureiq.springboot_app.infra.adapters.GmailApiService;
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
@RequestMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.GMAIL)
public class GmailApiController {

    private final GmailApiService gmailApiService;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public GmailApiController(GmailApiService gmailApiService) {
        this.gmailApiService = gmailApiService;
    }

    public record SendEmailRequest(String to, String subject, String body) {}

    @PostMapping("/send")
    public ResponseEntity<?> sendEmail(@RequestBody SendEmailRequest request) {
        Span span = tracer.spanBuilder("REST.sendEmail").startSpan();
        try (Scope scope = span.makeCurrent()) {
            if (request.to() == null || request.to().trim().isEmpty()) {
                throw new IllegalArgumentException("Recipient 'to' address is required");
            }
            Message message = gmailApiService.sendEmail(request.to(), request.subject(), request.body());
            span.setStatus(StatusCode.OK);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(200, message));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> listMessages() {
        Span span = tracer.spanBuilder("REST.listMessages").startSpan();
        try (Scope scope = span.makeCurrent()) {
            List<Message> messages = gmailApiService.listMessages();
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, messages));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(500, e.getMessage()));
        } finally {
            span.end();
        }
    }
}
