package com.procureiq.springboot_app.features.notifications.worker;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.procureiq.springboot_app.features.notifications.entity.*;
import com.procureiq.springboot_app.features.notifications.repository.*;
import com.procureiq.springboot_app.shared.ports.NotificationSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
public class NotificationBackgroundWorker {

    private final ChannelDeliveryRepository deliveryRepository;
    private final EscalationRunRepository escalationRunRepository;
    private final EscalationPolicyRepository escalationPolicyRepository;
    private final NotificationSender notificationSender;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public NotificationBackgroundWorker(
            ChannelDeliveryRepository deliveryRepository,
            EscalationRunRepository escalationRunRepository,
            EscalationPolicyRepository escalationPolicyRepository,
            NotificationSender notificationSender) {
        this.deliveryRepository = deliveryRepository;
        this.escalationRunRepository = escalationRunRepository;
        this.escalationPolicyRepository = escalationPolicyRepository;
        this.notificationSender = notificationSender;
    }

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void processDeliveries() {
        
        List<ChannelDelivery> pendingDeliveries = deliveryRepository
            .findByStatusAndNextRetryAtBefore("queued", Instant.now());

        for (ChannelDelivery delivery : pendingDeliveries) {
            delivery.setAttemptCount((short) (delivery.getAttemptCount() + 1));
            delivery.setUpdatedAt(Instant.now());

            try {
                
                notificationSender.send(
                    delivery.getChannel(),
                    delivery.getProvider(),
                    "user-" + delivery.getUserId() + "-endpoint",
                    "Alert Detail",
                    "A critical update has occurred on the system."
                );

                
                delivery.setStatus("delivered");
                delivery.setSentAt(Instant.now());
                delivery.setDeliveredAt(Instant.now());
            } catch (Exception e) {
                
                if (delivery.getAttemptCount() >= delivery.getMaxAttempts()) {
                    delivery.setStatus("failed");
                    delivery.setErrorCode("MAX_ATTEMPTS_EXCEEDED");
                    delivery.setErrorDetail(e.getMessage());
                } else {
                    
                    long backoffSeconds = (long) Math.pow(2, delivery.getAttemptCount()) * 5;
                    delivery.setNextRetryAt(Instant.now().plus(backoffSeconds, ChronoUnit.SECONDS));
                    delivery.setErrorCode("DELIVERY_FAILED");
                    delivery.setErrorDetail(e.getMessage());
                }
            }
            deliveryRepository.save(delivery);
        }
    }

    @Scheduled(fixedDelay = 10000)
    @Transactional
    public void processEscalations() {
        
        List<EscalationRun> activeRuns = escalationRunRepository.findByStatus("active");

        for (EscalationRun run : activeRuns) {
            if (run.getAcknowledgedAt() != null) {
                run.setStatus("acknowledged");
                run.setUpdatedAt(Instant.now());
                escalationRunRepository.save(run);
                continue;
            }

            Optional<EscalationPolicy> policyOpt = escalationPolicyRepository.findById(run.getPolicyId());
            if (policyOpt.isEmpty()) {
                continue;
            }

            EscalationPolicy policy = policyOpt.get();
            try {
                
                List<Map<String, Object>> steps = objectMapper.readValue(
                    policy.getSteps(),
                    new TypeReference<List<Map<String, Object>>>() {}
                );

                int nextStepIdx = run.getCurrentStep() + 1;
                if (nextStepIdx >= steps.size()) {
                    
                    run.setStatus("exhausted");
                    run.setUpdatedAt(Instant.now());
                    escalationRunRepository.save(run);
                    continue;
                }

                Map<String, Object> nextStep = steps.get(nextStepIdx);
                int waitMinutes = ((Number) nextStep.get("wait_minutes")).intValue();
                String channel = (String) nextStep.get("channel");

                Instant triggerTime = run.getCreatedAt().plus(waitMinutes, ChronoUnit.MINUTES);
                if (Instant.now().isAfter(triggerTime)) {
                    
                    ChannelDelivery delivery = new ChannelDelivery();
                    delivery.setId(System.currentTimeMillis() + new Random().nextInt(1000));
                    delivery.setNotificationId(run.getNotificationId());
                    delivery.setNotificationCreatedAt(run.getNotificationCreatedAt());
                    delivery.setUserId(run.getUserId());
                    delivery.setChannel(channel);
                    delivery.setProvider(getProviderForChannel(channel));
                    delivery.setStatus("queued");
                    delivery.setNextRetryAt(Instant.now());
                    delivery.setCreatedAt(Instant.now());
                    delivery.setUpdatedAt(Instant.now());
                    deliveryRepository.save(delivery);

                    
                    run.setCurrentStep((short) nextStepIdx);
                    run.setUpdatedAt(Instant.now());
                    escalationRunRepository.save(run);
                }
            } catch (Exception e) {
                
                run.setStatus("failed");
                run.setUpdatedAt(Instant.now());
                escalationRunRepository.save(run);
            }
        }
    }

    private String getProviderForChannel(String channel) {
        if ("sms".equalsIgnoreCase(channel)) {
            return "twilio";
        } else if ("email".equalsIgnoreCase(channel)) {
            return "sendgrid";
        } else {
            return "fcm";
        }
    }
}
