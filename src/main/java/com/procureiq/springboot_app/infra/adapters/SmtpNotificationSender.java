package com.procureiq.springboot_app.infra.adapters;

import com.procureiq.springboot_app.shared.ports.NotificationSender;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component("smtpNotificationSender")
public class SmtpNotificationSender implements NotificationSender {

    private static final Logger logger = LoggerFactory.getLogger(SmtpNotificationSender.class);
    private static final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app-infra", "1.0.0");

    private final JavaMailSender mailSender;

    public SmtpNotificationSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void send(String channel, String provider, String endpoint, String subject, String body) throws Exception {
        if (!"email".equalsIgnoreCase(channel) || !"smtp".equalsIgnoreCase(provider)) {
            return;
        }

        Span span = tracer.spanBuilder("VendorCall.smtp.send")
                .setAttribute("channel", channel)
                .setAttribute("provider", provider)
                .setAttribute("recipient", endpoint)
                .startSpan();

        try (Scope scope = span.makeCurrent()) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(endpoint);
            message.setSubject(subject);
            message.setText(body);
            message.setFrom("noreply@procureiq.com");
            mailSender.send(message);

            logger.info("Email sent to {} successfully via SMTP.", endpoint);
            span.setStatus(StatusCode.OK);
        } catch (Exception e) {
            logger.warn("Failed to send email to {} via SMTP. Error: {}", endpoint, e.getMessage());
            span.recordException(e);
            span.setStatus(StatusCode.ERROR, e.getMessage());
            throw e;
        } finally {
            span.end();
        }
    }
}
