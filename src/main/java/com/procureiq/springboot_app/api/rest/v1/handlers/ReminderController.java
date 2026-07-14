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
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            List<Reminder> reminders = reminderRepository.findAll();
            return ResponseEntity.ok(ApiResponse.success(200, reminders));
        });
    }

    @PostMapping
    public ResponseEntity<?> createReminder(@jakarta.validation.Valid @RequestBody Reminder reminder) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            if (reminder.getCreatedAt() == null) {
                reminder.setCreatedAt(Instant.now());
            }
            if (reminder.getStatus() == null) {
                reminder.setStatus("PENDING");
            }
            Reminder saved = reminderRepository.save(reminder);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(201, saved));
        });
    }

    @PutMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> updateReminder(@PathVariable Long id, @jakarta.validation.Valid @RequestBody Reminder details) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
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
            return ResponseEntity.ok(ApiResponse.success(200, saved));
        });
    }

    @DeleteMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> deleteReminder(@PathVariable Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            reminderRepository.deleteById(id);
            return ResponseEntity.ok(ApiResponse.success(200, "Reminder deleted successfully"));
        });
    }
}
