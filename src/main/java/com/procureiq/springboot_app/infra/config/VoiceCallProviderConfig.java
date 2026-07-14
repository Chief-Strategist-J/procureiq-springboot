package com.procureiq.springboot_app.infra.config;

import com.procureiq.springboot_app.infra.adapters.voice.MockVoiceAdapter;
import com.procureiq.springboot_app.infra.adapters.voice.TwilioVoiceAdapter;
import com.procureiq.springboot_app.infra.adapters.voice.VapiVoiceAdapter;
import com.procureiq.springboot_app.shared.ports.VoiceCallPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


@Configuration
public class VoiceCallProviderConfig {

    private final MockVoiceAdapter mockVoiceAdapter;
    private final TwilioVoiceAdapter twilioVoiceAdapter;
    private final VapiVoiceAdapter vapiVoiceAdapter;

    public VoiceCallProviderConfig(
            MockVoiceAdapter mockVoiceAdapter,
            TwilioVoiceAdapter twilioVoiceAdapter,
            VapiVoiceAdapter vapiVoiceAdapter
    ) {
        this.mockVoiceAdapter   = mockVoiceAdapter;
        this.twilioVoiceAdapter = twilioVoiceAdapter;
        this.vapiVoiceAdapter   = vapiVoiceAdapter;
    }

    @Bean
    @Primary
    public VoiceCallPort voiceCallPort() {
        String provider = System.getenv("VOICE_PROVIDER");
        if (provider == null) {
            provider = "";
        }
        return switch (provider.trim().toLowerCase()) {
            case "twilio" -> {
                System.out.println("[VOICE] Provider selected: Twilio");
                yield twilioVoiceAdapter;
            }
            case "vapi" -> {
                System.out.println("[VOICE] Provider selected: Vapi");
                yield vapiVoiceAdapter;
            }
            default -> {
                System.out.println("[VOICE] Provider selected: Mock (default)");
                yield mockVoiceAdapter;
            }
        };
    }
}
