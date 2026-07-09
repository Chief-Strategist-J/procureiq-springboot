package com.procureiq.springboot_app.features.notifications.repository;

import com.procureiq.springboot_app.features.notifications.entity.EscalationRun;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EscalationRunRepository extends JpaRepository<EscalationRun, Long> {
    List<EscalationRun> findByStatus(String status);
}
