package com.procureiq.springboot_app.features.notifications.repository;

import com.procureiq.springboot_app.features.notifications.entity.Notification;
import com.procureiq.springboot_app.features.notifications.entity.NotificationId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, NotificationId> {
    List<Notification> findByTargetScopeAndGlobalSeqGreaterThanOrderByGlobalSeqDesc(String targetScope, Long globalSeq);
    boolean existsByTypeIdAndDedupKey(Short typeId, String dedupKey);
}
