package com.procureiq.springboot_app.features.notifications.repository;

import com.procureiq.springboot_app.features.notifications.entity.NotificationRecipient;
import com.procureiq.springboot_app.features.notifications.entity.NotificationRecipientId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.Optional;

public interface NotificationRecipientRepository extends JpaRepository<NotificationRecipient, NotificationRecipientId> {
    Page<NotificationRecipient> findByUserIdAndDeletedAtIsNull(Long userId, Pageable pageable);
    Page<NotificationRecipient> findByUserIdAndStatusAndDeletedAtIsNull(Long userId, String status, Pageable pageable);
    long countByUserIdAndStatusAndDeletedAtIsNull(Long userId, String status);
    Optional<NotificationRecipient> findByUserIdAndNotificationId(Long userId, Long notificationId);

    @Query("SELECT COUNT(r) FROM NotificationRecipient r, Notification n " +
           "WHERE r.notificationId = n.id AND r.userId = :userId AND r.status = :status AND r.deletedAt IS NULL " +
           "AND (n.scheduledFor IS NULL OR n.scheduledFor <= :now)")
    long countUnreadTargetedActive(@Param("userId") Long userId, @Param("status") String status, @Param("now") Instant now);
}
