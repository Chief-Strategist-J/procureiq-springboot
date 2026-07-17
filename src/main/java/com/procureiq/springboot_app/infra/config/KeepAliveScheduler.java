package com.procureiq.springboot_app.infra.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Sends a self-ping to /actuator/health every 10 minutes to prevent
 * the Render free-tier instance from spinning down due to inactivity.
 *
 * Set the RENDER_EXTERNAL_URL env var in render.yaml to activate.
 * Example: https://procureiq-springboot.onrender.com
 */
@Component
public class KeepAliveScheduler {

    private static final Logger log = LoggerFactory.getLogger(KeepAliveScheduler.class);
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${render.external.url:}")
    private String renderExternalUrl;

    // Fires every 10 minutes (600,000 ms), first ping after 1 minute
    @Scheduled(fixedDelay = 600_000, initialDelay = 60_000)
    public void keepAlive() {
        if (renderExternalUrl == null || renderExternalUrl.isBlank()) {
            return; // Disabled — set RENDER_EXTERNAL_URL env var to activate
        }
        String healthUrl = renderExternalUrl.stripTrailing() + "/actuator/health";
        try {
            restTemplate.getForObject(healthUrl, String.class);
            log.debug("[KeepAlive] Ping sent to {}", healthUrl);
        } catch (Exception e) {
            log.warn("[KeepAlive] Ping failed: {}", e.getMessage());
        }
    }
}
