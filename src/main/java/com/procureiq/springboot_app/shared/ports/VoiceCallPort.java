package com.procureiq.springboot_app.shared.ports;

/**
 * Port (interface) for provider-agnostic voice call dispatch.
 * Implementations: MockVoiceAdapter, TwilioVoiceAdapter, VapiVoiceAdapter.
 * The active implementation is selected at startup via VOICE_PROVIDER env var.
 */
public interface VoiceCallPort {
    /**
     * Initiate an outbound voice call to the given phone number
     * and speak the supplied instructions.
     *
     * @param phoneNumber E.164-formatted destination number (e.g. "+15551234567")
     * @param instructions Spoken text or TwiML/SSML to deliver to the callee
     * @throws Exception if the underlying provider call fails
     */
    void call(String phoneNumber, String instructions) throws Exception;
}
