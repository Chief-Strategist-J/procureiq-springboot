package com.procureiq.springboot_app.infra.adapters;

import com.procureiq.springboot_app.shared.ports.NotificationSender;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.springframework.stereotype.Component;

@Component
public class MockNotificationSender implements NotificationSender {

    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app-infra", "1.0.0");

    @Override
    public void send(String channel, String provider, String endpoint, String subject, String body) throws Exception {
        Span span = tracer.spanBuilder("VendorCall." + provider + ".send").startSpan();
        span.setAttribute("channel", channel);
        span.setAttribute("provider", provider);

        try (Scope scope = span.makeCurrent()) {
            // Simulate networking delays
            Thread.sleep(50);
            
            System.out.println(String.format(
                "[MOCK SEND] Channel: %s | Provider: %s | Target: %s | Subject: %s | Body length: %d",
                channel, provider, endpoint, subject, body != null ? body.length() : 0
            ));
            
            span.setStatus(StatusCode.OK);
        } catch (Exception e) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR, e.getMessage());
            throw e;
        } finally {
            span.end();
        }
    }
}
