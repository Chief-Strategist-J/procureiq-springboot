package com.procureiq.springboot_app.features.jobs.repository;

import com.procureiq.springboot_app.features.jobs.entity.Workflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowRepository extends JpaRepository<Workflow, Long> {
}
