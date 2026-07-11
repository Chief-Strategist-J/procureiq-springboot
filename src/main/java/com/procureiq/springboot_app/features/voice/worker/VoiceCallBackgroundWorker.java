package com.procureiq.springboot_app.features.voice.worker;

import com.procureiq.springboot_app.features.voice.entity.ScheduledCall;
import com.procureiq.springboot_app.features.voice.repository.ScheduledCallRepository;
import com.procureiq.springboot_app.shared.ports.VoiceCallPort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * VoiceCallBackgroundWorker — polls the scheduled_calls table every 5 seconds
 * for PENDING calls whose scheduledAt time has passed, then routes each call
 * through the active VoiceCallPort adapter (mock/twilio/vapi).
 */
@Component
public class VoiceCallBackgroundWorker {

    private final ScheduledCallRepository scheduledCallRepository;
    private final VoiceCallPort voiceCallPort;

    public VoiceCallBackgroundWorker(
            ScheduledCallRepository scheduledCallRepository,
            VoiceCallPort voiceCallPort
    ) {
        this.scheduledCallRepository = scheduledCallRepository;
        this.voiceCallPort           = voiceCallPort;
    }

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void processScheduledCalls() {
        List<ScheduledCall> dueCalls =
            scheduledCallRepository.findByStatusAndScheduledAtBefore("PENDING", Instant.now());

        for (ScheduledCall call : dueCalls) {
            try {
                voiceCallPort.call(call.getPhoneNumber(), call.getInstructions());
                call.setStatus("CALLED");
            } catch (Exception e) {
                System.err.printf(
                    "[VoiceCallWorker] Call id=%d to %s FAILED: %s%n",
                    call.getId(), call.getPhoneNumber(), e.getMessage()
                );
                call.setStatus("FAILED");
            }
            scheduledCallRepository.save(call);
        }
    }
}
