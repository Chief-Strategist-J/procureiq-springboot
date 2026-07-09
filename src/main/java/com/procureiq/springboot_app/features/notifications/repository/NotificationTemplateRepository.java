package com.procureiq.springboot_app.features.notifications.repository;

import com.procureiq.springboot_app.features.notifications.entity.NotificationTemplate;
import com.procureiq.springboot_app.features.notifications.entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long> {
    Optional<NotificationTemplate> findByTypeAndChannelAndIsActiveTrue(NotificationType type, String channel);
}
