package com.procureiq.springboot_app.infra.adapters.voice;

import com.procureiq.springboot_app.shared.ports.VoiceCallPort;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component("vapiVoiceAdapter")
public class VapiVoiceAdapter implements VoiceCallPort {

    @org.springframework.beans.factory.annotation.Autowired
    private com.procureiq.springboot_app.infra.config.AppProperties appProperties;

    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app-infra", "1.0.0");
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void call(String phoneNumber, String instructions) throws Exception {
        Span span = tracer.spanBuilder("VoiceCall.vapi.call").startSpan();
        span.setAttribute("voice.provider", "vapi");
        span.setAttribute("voice.phone_number", phoneNumber != null ? phoneNumber : "");

        try (Scope scope = span.makeCurrent()) {
            String apiKey         = System.getenv("VAPI_API_KEY");
            String phoneNumberId  = System.getenv("VAPI_PHONE_NUMBER_ID");
            String assistantId    = System.getenv("VAPI_ASSISTANT_ID");

            if (apiKey == null || phoneNumberId == null || assistantId == null) {
                throw new IllegalStateException(
                    "VAPI_API_KEY, VAPI_PHONE_NUMBER_ID, and VAPI_ASSISTANT_ID must be set"
                );
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> payload = new HashMap<>();
            payload.put("assistantId", assistantId);
            payload.put("phoneNumberId", phoneNumberId);
            payload.put("customer", Map.of("number", phoneNumber));

            Map<String, Object> assistantOverrides = new HashMap<>();
            assistantOverrides.put("firstMessage", instructions);
            payload.put("assistantOverrides", assistantOverrides);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            restTemplate.postForObject(appProperties.getVapiCallsEndpoint(), request, String.class);

            span.setStatus(StatusCode.OK);
            System.out.printf("[VAPI VOICE CALL] Phone: %s | Instructions: %s%n", phoneNumber, instructions);
        } catch (Exception e) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR, e.getMessage());
            throw e;
        } finally {
            span.end();
        }
    }
}
