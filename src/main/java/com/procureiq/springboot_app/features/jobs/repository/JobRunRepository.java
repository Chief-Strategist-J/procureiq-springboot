package com.procureiq.springboot_app.features.jobs.repository;

import com.procureiq.springboot_app.features.jobs.entity.JobRun;
import com.procureiq.springboot_app.features.jobs.entity.JobRunId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobRunRepository extends JpaRepository<JobRun, JobRunId> {
    List<JobRun> findByJobId(Long jobId);

    @Query("SELECT jr FROM JobRun jr WHERE jr.id = :id")
    Optional<JobRun> findByRawId(@Param("id") Long id);
}
