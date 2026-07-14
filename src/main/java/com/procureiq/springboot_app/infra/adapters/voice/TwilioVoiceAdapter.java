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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component("twilioVoiceAdapter")
public class TwilioVoiceAdapter implements VoiceCallPort {

    @org.springframework.beans.factory.annotation.Autowired
    private com.procureiq.springboot_app.infra.config.AppProperties appProperties;

    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app-infra", "1.0.0");
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void call(String phoneNumber, String instructions) throws Exception {
        Span span = tracer.spanBuilder("VoiceCall.twilio.call").startSpan();
        span.setAttribute("voice.provider", "twilio");
        span.setAttribute("voice.phone_number", phoneNumber != null ? phoneNumber : "");

        try (Scope scope = span.makeCurrent()) {
            String accountSid = System.getenv("TWILIO_ACCOUNT_SID");
            String authToken  = System.getenv("TWILIO_AUTH_TOKEN");
            String fromNumber = System.getenv("TWILIO_FROM_NUMBER");

            if (accountSid == null || authToken == null || fromNumber == null) {
                throw new IllegalStateException(
                    "TWILIO_ACCOUNT_SID, TWILIO_AUTH_TOKEN, and TWILIO_FROM_NUMBER must be set"
                );
            }

            String credentials = accountSid + ":" + authToken;
            String encodedCredentials = Base64.getEncoder()
                .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Authorization", "Basic " + encodedCredentials);

            String twiml = "<Response><Say>" + escapeXml(instructions) + "</Say></Response>";

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("To", phoneNumber);
            body.add("From", fromNumber);
            body.add("Twiml", twiml);

            String url = appProperties.getTwilioApiBase() + "/" + accountSid + "/Calls.json";
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

            restTemplate.postForObject(url, request, String.class);

            span.setStatus(StatusCode.OK);
            System.out.printf("[TWILIO VOICE CALL] Phone: %s | Instructions: %s%n", phoneNumber, instructions);
        } catch (Exception e) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR, e.getMessage());
            throw e;
        } finally {
            span.end();
        }
    }

    private String escapeXml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&apos;");
    }
}
