package com.procureiq.springboot_app.features.jobs.repository;

import com.procureiq.springboot_app.features.jobs.entity.WorkflowRun;
import com.procureiq.springboot_app.features.jobs.entity.WorkflowRunId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkflowRunRepository extends JpaRepository<WorkflowRun, WorkflowRunId> {
    List<WorkflowRun> findByWorkflowId(Long workflowId);

    @Query("SELECT wr FROM WorkflowRun wr WHERE wr.id = :id")
    Optional<WorkflowRun> findByRawId(@Param("id") Long id);
}
