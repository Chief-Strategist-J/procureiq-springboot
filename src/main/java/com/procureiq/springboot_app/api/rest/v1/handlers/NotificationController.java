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
        Span span = tracer.spanBuilder("REST.getNotifications").startSpan();
        try (Scope scope = span.makeCurrent()) {
            NotificationListResponse response = notificationService.getNotifications(userId, status, page, size);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(500, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @PostMapping
    public ResponseEntity<?> sendNotification(@RequestBody SendNotificationRequest request) {
        Span span = tracer.spanBuilder("REST.sendNotification").startSpan();
        try (Scope scope = span.makeCurrent()) {
            notificationService.sendNotification(request);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(ApiResponse.success(202, "Notification accepted for delivery"));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @PutMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.STATUS_ID)
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestHeader(name = "X-User-Id", defaultValue = "1") Long userId,
            @RequestBody UpdateNotificationStatusRequest request) {
        Span span = tracer.spanBuilder("REST.updateStatus").startSpan();
        try (Scope scope = span.makeCurrent()) {
            notificationService.updateStatus(userId, id, request.status());
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, "Status updated successfully"));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @GetMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.UNREAD_COUNT)
    public ResponseEntity<?> getUnreadCount(
            @RequestHeader(name = "X-User-Id", defaultValue = "1") Long userId) {
        Span span = tracer.spanBuilder("REST.getUnreadCount").startSpan();
        try (Scope scope = span.makeCurrent()) {
            UnreadCountResponse response = notificationService.getUnreadCount(userId);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(500, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @PostMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.DEVICES)
    public ResponseEntity<?> registerDevice(
            @RequestHeader(name = "X-User-Id", defaultValue = "1") Long userId,
            @RequestBody RegisterDeviceRequest request) {
        Span span = tracer.spanBuilder("REST.registerDevice").startSpan();
        try (Scope scope = span.makeCurrent()) {
            notificationService.registerDevice(userId, request);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, "Device registered successfully"));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }
}
