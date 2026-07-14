package com.procureiq.springboot_app.api.rest.v1.handlers;

import com.procureiq.springboot_app.features.reminders.entity.Reminder;
import com.procureiq.springboot_app.features.reminders.repository.ReminderRepository;
import com.procureiq.springboot_app.shared.types.ApiResponse;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.REMINDERS)
@CrossOrigin(origins = "*")
public class ReminderController {

    private final ReminderRepository reminderRepository;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public ReminderController(ReminderRepository reminderRepository) {
        this.reminderRepository = reminderRepository;
    }

    @GetMapping
    public ResponseEntity<?> getAllReminders() {
        Span span = tracer.spanBuilder("REST.getAllReminders").startSpan();
        try (Scope scope = span.makeCurrent()) {
            List<Reminder> reminders = reminderRepository.findAll();
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, reminders));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @PostMapping
    public ResponseEntity<?> createReminder(@RequestBody Reminder reminder) {
        Span span = tracer.spanBuilder("REST.createReminder").startSpan();
        try (Scope scope = span.makeCurrent()) {
            if (reminder.getCreatedAt() == null) {
                reminder.setCreatedAt(Instant.now());
            }
            if (reminder.getStatus() == null) {
                reminder.setStatus("PENDING");
            }
            Reminder saved = reminderRepository.save(reminder);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(201, saved));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReminder(@PathVariable Long id, @RequestBody Reminder details) {
        Span span = tracer.spanBuilder("REST.updateReminder").startSpan();
        try (Scope scope = span.makeCurrent()) {
            Reminder existing = reminderRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Reminder not found with ID: " + id));
            
            if (details.getTitle() != null) existing.setTitle(details.getTitle());
            if (details.getDescription() != null) existing.setDescription(details.getDescription());
            if (details.getDueAt() != null) existing.setDueAt(details.getDueAt());
            if (details.getRecurrence() != null) existing.setRecurrence(details.getRecurrence());
            if (details.getPriority() != null) existing.setPriority(details.getPriority());
            if (details.getContactPreference() != null) existing.setContactPreference(details.getContactPreference());
            if (details.getAssigneeName() != null) existing.setAssigneeName(details.getAssigneeName());
            if (details.getAssigneeContact() != null) existing.setAssigneeContact(details.getAssigneeContact());
            if (details.getStatus() != null) existing.setStatus(details.getStatus());
            if (details.getSnoozeCount() != null) existing.setSnoozeCount(details.getSnoozeCount());
            
            Reminder saved = reminderRepository.save(existing);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, saved));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReminder(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.deleteReminder").startSpan();
        try (Scope scope = span.makeCurrent()) {
            reminderRepository.deleteById(id);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, "Reminder deleted successfully"));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }
}
