package com.procureiq.springboot_app.features.reminders.worker;

import com.procureiq.springboot_app.features.reminders.entity.Reminder;
import com.procureiq.springboot_app.features.reminders.repository.ReminderRepository;
import com.procureiq.springboot_app.infra.adapters.GmailApiService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Component
public class ReminderBackgroundWorker {

    private final ReminderRepository reminderRepository;
    private final GmailApiService gmailApiService;

    public ReminderBackgroundWorker(ReminderRepository reminderRepository, GmailApiService gmailApiService) {
        this.reminderRepository = reminderRepository;
        this.gmailApiService = gmailApiService;
    }

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void processReminders() {
        
        List<Reminder> dueReminders = reminderRepository.findByStatusAndDueAtBefore("PENDING", Instant.now());

        for (Reminder reminder : dueReminders) {
            String preference = reminder.getContactPreference();
            if ("EMAIL".equalsIgnoreCase(preference) || "GMAIL".equalsIgnoreCase(preference)) {
                try {
                    
                    gmailApiService.sendEmail(
                        reminder.getAssigneeContact(),
                        "Reminder: " + reminder.getTitle(),
                        reminder.getDescription()
                    );
                    reminder.setStatus("SENT");
                } catch (Exception e) {
                    reminder.setStatus("FAILED");
                }
            } else {
                
                reminder.setStatus("COMPLETED");
            }
            reminderRepository.save(reminder);
        }
    }
}
