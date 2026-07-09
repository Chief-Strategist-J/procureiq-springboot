package com.procureiq.springboot_app.features.notifications.repository;

import com.procureiq.springboot_app.features.notifications.entity.EscalationPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EscalationPolicyRepository extends JpaRepository<EscalationPolicy, Long> {
}
