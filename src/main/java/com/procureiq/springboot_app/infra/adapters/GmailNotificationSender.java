package com.procureiq.springboot_app.infra.adapters;

import com.procureiq.springboot_app.shared.ports.NotificationSender;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("gmailNotificationSender")
public class GmailNotificationSender implements NotificationSender {

    private static final Logger logger = LoggerFactory.getLogger(GmailNotificationSender.class);
    private static final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app-infra", "1.0.0");

    private final GmailApiService gmailApiService;

    public GmailNotificationSender(GmailApiService gmailApiService) {
        this.gmailApiService = gmailApiService;
    }

    @Override
    public void send(String channel, String provider, String endpoint, String subject, String body) throws Exception {
        if (!"email".equalsIgnoreCase(channel) || !"gmail".equalsIgnoreCase(provider)) {
            return;
        }

        Span span = tracer.spanBuilder("VendorCall.gmail.send")
                .setAttribute("channel", channel)
                .setAttribute("provider", provider)
                .setAttribute("recipient", endpoint)
                .startSpan();

        try (Scope scope = span.makeCurrent()) {
            gmailApiService.sendEmail(endpoint, subject, body);
            logger.info("Email sent to {} successfully via Gmail API.", endpoint);
            span.setStatus(StatusCode.OK);
        } catch (Exception e) {
            logger.warn("Failed to send email to {} via Gmail API. Error: {}", endpoint, e.getMessage());
            span.recordException(e);
            span.setStatus(StatusCode.ERROR, e.getMessage());
            throw e;
        } finally {
            span.end();
        }
    }
}
