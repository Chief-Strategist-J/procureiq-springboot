package com.procureiq.springboot_app.api.rest.v1.handlers;

import com.procureiq.springboot_app.features.notifications.dto.*;
import com.procureiq.springboot_app.features.notifications.service.NotificationService;
import com.procureiq.springboot_app.shared.types.ApiResponse;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.NOTIFICATIONS)
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService notificationService;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<?> getNotifications(
            @RequestHeader(name = "X-User-Id", defaultValue = "1") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "all") String status) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            NotificationListResponse response = notificationService.getNotifications(userId, status, page, size);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        });
    }

    @PostMapping
    public ResponseEntity<?> sendNotification(@jakarta.validation.Valid @RequestBody SendNotificationRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            notificationService.sendNotification(request);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(ApiResponse.success(202, "Notification accepted for delivery"));
        });
    }

    @PutMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.STATUS_ID)
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestHeader(name = "X-User-Id", defaultValue = "1") Long userId,
            @jakarta.validation.Valid @RequestBody UpdateNotificationStatusRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            notificationService.updateStatus(userId, id, request.status());
            return ResponseEntity.ok(ApiResponse.success(200, "Status updated successfully"));
        });
    }

    @GetMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.UNREAD_COUNT)
    public ResponseEntity<?> getUnreadCount(
            @RequestHeader(name = "X-User-Id", defaultValue = "1") Long userId) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            UnreadCountResponse response = notificationService.getUnreadCount(userId);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        });
    }

    @PostMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.DEVICES)
    public ResponseEntity<?> registerDevice(
            @RequestHeader(name = "X-User-Id", defaultValue = "1") Long userId,
            @jakarta.validation.Valid @RequestBody RegisterDeviceRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            notificationService.registerDevice(userId, request);
            return ResponseEntity.ok(ApiResponse.success(200, "Device registered successfully"));
        });
    }
}
