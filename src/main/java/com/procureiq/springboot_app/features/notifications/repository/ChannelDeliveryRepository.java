package com.procureiq.springboot_app.features.notifications.repository;

import com.procureiq.springboot_app.features.notifications.entity.ChannelDelivery;
import com.procureiq.springboot_app.features.notifications.entity.ChannelDeliveryId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.List;

public interface ChannelDeliveryRepository extends JpaRepository<ChannelDelivery, ChannelDeliveryId> {
    List<ChannelDelivery> findByStatusAndNextRetryAtBefore(String status, Instant time);
}
