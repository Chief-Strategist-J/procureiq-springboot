package com.procureiq.springboot_app.infra.adapters.voice;

import com.procureiq.springboot_app.infra.clients.CentralHttpClient;
import com.procureiq.springboot_app.infra.config.ApiEndpoints;
import com.procureiq.springboot_app.infra.config.AppProperties;
import com.procureiq.springboot_app.infra.config.TracingHelper;
import com.procureiq.springboot_app.shared.ports.VoiceCallPort;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Optional;

@Component("twilioVoiceAdapter")
public class TwilioVoiceAdapter implements VoiceCallPort {

    private final AppProperties      appProperties;
    private final CentralHttpClient  httpClient;

    public TwilioVoiceAdapter(AppProperties appProperties, CentralHttpClient httpClient) {
        this.appProperties = appProperties;
        this.httpClient    = httpClient;
    }

    @Override
    public void call(String phoneNumber, String instructions) throws Exception {
        TracingHelper.executeServiceWithTracing(() -> {

            String accountSid = resolve(ApiEndpoints.TWILIO_ENV_ACCOUNT_SID);
            String authToken  = resolve(ApiEndpoints.TWILIO_ENV_AUTH_TOKEN);
            String fromNumber = resolve(ApiEndpoints.TWILIO_ENV_FROM_NUMBER);

            String url = appProperties.getTwilioApiBase()
                    + ApiEndpoints.PATH_ID.replace("{id}", accountSid)
                    + ApiEndpoints.TWILIO_CALLS_PATH;

            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add(ApiEndpoints.TWILIO_FORM_TO,    phoneNumber);
            form.add(ApiEndpoints.TWILIO_FORM_FROM,  fromNumber);
            form.add(ApiEndpoints.TWILIO_FORM_TWIML, buildTwiml(instructions));

            httpClient.postForm(url, form, accountSid, authToken, String.class);
            return null;
        });
    }

    private String resolve(String envKey) {
        return Optional.ofNullable(System.getenv(envKey))
                .filter(v -> !v.isBlank())
                .orElseThrow(() -> new IllegalStateException(envKey + " environment variable must be set"));
    }

    private String buildTwiml(String text) {
        return "<Response><Say>" + escapeXml(text) + "</Say></Response>";
    }

    private String escapeXml(String text) {
        return Optional.ofNullable(text).orElse("")
                .replace("&",  "&amp;")
                .replace("<",  "&lt;")
                .replace(">",  "&gt;")
                .replace("\"", "&quot;")
                .replace("'",  "&apos;");
    }
}
