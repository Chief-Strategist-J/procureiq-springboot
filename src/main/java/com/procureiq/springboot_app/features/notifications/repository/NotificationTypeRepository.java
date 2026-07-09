package com.procureiq.springboot_app.features.notifications.repository;

import com.procureiq.springboot_app.features.notifications.entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface NotificationTypeRepository extends JpaRepository<NotificationType, Short> {
    Optional<NotificationType> findByCode(String code);
}
