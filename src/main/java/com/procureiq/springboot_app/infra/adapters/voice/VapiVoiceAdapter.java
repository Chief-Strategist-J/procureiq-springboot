package com.procureiq.springboot_app.infra.adapters.voice;

import com.procureiq.springboot_app.infra.clients.CentralHttpClient;
import com.procureiq.springboot_app.infra.config.ApiEndpoints;
import com.procureiq.springboot_app.infra.config.AppProperties;
import com.procureiq.springboot_app.infra.config.TracingHelper;
import com.procureiq.springboot_app.shared.ports.VoiceCallPort;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component("vapiVoiceAdapter")
public class VapiVoiceAdapter implements VoiceCallPort {

    private final AppProperties       appProperties;
    private final CentralHttpClient   httpClient;

    public VapiVoiceAdapter(AppProperties appProperties, CentralHttpClient httpClient) {
        this.appProperties = appProperties;
        this.httpClient    = httpClient;
    }

    @Override
    public void call(String phoneNumber, String instructions) throws Exception {
        TracingHelper.executeServiceWithTracing(() -> {

            String apiKey        = resolve(ApiEndpoints.VAPI_ENV_API_KEY);
            String phoneNumberId = resolve(ApiEndpoints.VAPI_ENV_PHONE_NUMBER_ID);
            String assistantId   = resolve(ApiEndpoints.VAPI_ENV_ASSISTANT_ID);

            Map<String, Object> payload = Map.of(
                    ApiEndpoints.VAPI_KEY_ASSISTANT_ID,    assistantId,
                    ApiEndpoints.VAPI_KEY_PHONE_NUMBER_ID, phoneNumberId,
                    ApiEndpoints.VAPI_KEY_CUSTOMER,        Map.of(ApiEndpoints.VAPI_KEY_CUSTOMER_NUMBER, phoneNumber),
                    ApiEndpoints.VAPI_KEY_OVERRIDES,       Map.of(ApiEndpoints.VAPI_KEY_FIRST_MESSAGE, instructions)
            );

            httpClient.postWithBearer(appProperties.getVapiCallsEndpoint(), payload, apiKey, String.class);
            return null;
        });
    }

    private String resolve(String envKey) {
        return Optional.ofNullable(System.getenv(envKey))
                .filter(v -> !v.isBlank())
                .orElseThrow(() -> new IllegalStateException(envKey + " environment variable must be set"));
    }
}
