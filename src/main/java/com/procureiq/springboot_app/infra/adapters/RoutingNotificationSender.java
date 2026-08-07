package com.procureiq.springboot_app.infra.adapters;

import com.procureiq.springboot_app.shared.ports.NotificationSender;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@Primary
public class RoutingNotificationSender implements NotificationSender {

    private static final Logger logger = LoggerFactory.getLogger(RoutingNotificationSender.class);
    private static final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app-infra", "1.0.0");

    private final List<NotificationSender> senders;
    private final MockNotificationSender mockNotificationSender;

    public RoutingNotificationSender(List<NotificationSender> senders, MockNotificationSender mockNotificationSender) {
        this.senders = senders;
        this.mockNotificationSender = mockNotificationSender;
    }

    @Override
    public void send(String channel, String provider, String endpoint, String subject, String body) throws Exception {
        Span span = tracer.spanBuilder("Notification.Send")
                .setAttribute("channel", channel)
                .setAttribute("provider", provider)
                .setAttribute("recipient", endpoint)
                .startSpan();

        try (Scope scope = span.makeCurrent()) {
            boolean handled = false;
            for (NotificationSender sender : senders) {
                if (sender instanceof RoutingNotificationSender) {
                    continue;
                }

                if (sender instanceof SmtpNotificationSender && "email".equalsIgnoreCase(channel) && "smtp".equalsIgnoreCase(provider)) {
                    sender.send(channel, provider, endpoint, subject, body);
                    handled = true;
                    break;
                }
                if (sender instanceof GmailNotificationSender && "email".equalsIgnoreCase(channel) && "gmail".equalsIgnoreCase(provider)) {
                    sender.send(channel, provider, endpoint, subject, body);
                    handled = true;
                    break;
                }
            }

            if (!handled) {
                logger.info("No concrete sender matched. Routing to MockNotificationSender for channel: {}, provider: {}", channel, provider);
                mockNotificationSender.send(channel, provider, endpoint, subject, body);
            }
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
