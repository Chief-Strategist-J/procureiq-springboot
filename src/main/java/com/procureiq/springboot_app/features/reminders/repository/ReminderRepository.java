package com.procureiq.springboot_app.features.reminders.repository;

import com.procureiq.springboot_app.features.reminders.entity.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.List;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    List<Reminder> findByStatusAndDueAtBefore(String status, Instant dueAt);
}
