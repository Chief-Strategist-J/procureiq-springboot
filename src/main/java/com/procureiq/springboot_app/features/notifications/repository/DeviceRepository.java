package com.procureiq.springboot_app.features.notifications.repository;

import com.procureiq.springboot_app.features.notifications.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    List<Device> findByUserIdAndIsActiveTrue(Long userId);
    Optional<Device> findByUserIdAndPlatformAndPushToken(Long userId, String platform, String pushToken);
}
