package com.procureiq.springboot_app.api.rest.v1.handlers;

import com.google.api.services.gmail.model.Message;
import com.procureiq.springboot_app.infra.adapters.GmailApiService;
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

    @PostMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.SEND)
    public ResponseEntity<?> sendEmail(@jakarta.validation.Valid @RequestBody SendEmailRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            if (request.to() == null || request.to().trim().isEmpty()) {
                throw new IllegalArgumentException("Recipient 'to' address is required");
            }
            Message message = gmailApiService.sendEmail(request.to(), request.subject(), request.body());
            return ResponseEntity.status(HttpStatus.OK).body(ApiSingleResponse.success(200, message));
        });
    }

    @GetMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.LIST)
    public ResponseEntity<?> listMessages() {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            List<Message> messages = gmailApiService.listMessages();
            return ResponseEntity.ok(ApiListResponse.success(200, messages));
        });
    }
}
