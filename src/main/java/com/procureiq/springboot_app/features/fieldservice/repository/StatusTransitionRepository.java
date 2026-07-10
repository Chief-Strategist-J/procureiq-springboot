package com.procureiq.springboot_app.features.fieldservice.repository;

import com.procureiq.springboot_app.features.fieldservice.entity.StatusTransition;
import com.procureiq.springboot_app.features.fieldservice.entity.StatusTransitionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusTransitionRepository extends JpaRepository<StatusTransition, StatusTransitionId> {
}
