package com.procureiq.springboot_app;

import com.procureiq.springboot_app.features.reminders.entity.Reminder;
import com.procureiq.springboot_app.features.reminders.repository.ReminderRepository;
import com.procureiq.springboot_app.features.reminders.worker.ReminderBackgroundWorker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ReminderBackgroundWorkerTest {

    @Autowired
    private ReminderRepository reminderRepository;

    @Autowired
    private ReminderBackgroundWorker reminderBackgroundWorker;

    @BeforeEach
    public void setup() {
        reminderRepository.deleteAll();
    }

    @Test
    public void testReminderProcessingAndGmailDispatch() {
        Reminder reminder = new Reminder();
        reminder.setTitle("Meeting with Product Team");
        reminder.setDescription("Discuss the Q3 launch specifications and data models.");
        reminder.setDueAt(Instant.now().minusSeconds(30));
        reminder.setContactPreference("EMAIL");
        reminder.setAssigneeName("Alice");
        reminder.setAssigneeContact("alice@example.com");
        reminder.setStatus("PENDING");
        reminderRepository.save(reminder);

        reminderBackgroundWorker.processReminders();

        List<Reminder> results = reminderRepository.findAll();
        assertEquals(1, results.size());
        assertEquals("SENT", results.get(0).getStatus());
    }
}
