package com.procureiq.springboot_app.features.notifications.repository;

import com.procureiq.springboot_app.features.notifications.entity.RoutingRule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RoutingRuleRepository extends JpaRepository<RoutingRule, Long> {
    List<RoutingRule> findByTypeIdAndIsActiveTrueOrderByPriorityOrderAsc(Short typeId);
}
