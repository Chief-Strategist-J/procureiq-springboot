package com.procureiq.springboot_app.infra.adapters.voice;

import com.procureiq.springboot_app.shared.ports.VoiceCallPort;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.springframework.stereotype.Component;

/**
 * MockVoiceAdapter — no-op voice adapter used when VOICE_PROVIDER=mock (default).
 * Logs the call details to stdout and returns immediately without hitting any API.
 */
@Component("mockVoiceAdapter")
public class MockVoiceAdapter implements VoiceCallPort {

    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app-infra", "1.0.0");

    @Override
    public void call(String phoneNumber, String instructions) throws Exception {
        Span span = tracer.spanBuilder("VoiceCall.mock.call").startSpan();
        span.setAttribute("voice.provider", "mock");
        span.setAttribute("voice.phone_number", phoneNumber != null ? phoneNumber : "");

        try (Scope scope = span.makeCurrent()) {
            System.out.printf(
                "[MOCK VOICE CALL] Phone: %s | Instructions: %s%n",
                phoneNumber,
                instructions != null ? instructions : "(none)"
            );
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
