package com.procureiq.springboot_app.features.voice.repository;

import com.procureiq.springboot_app.features.voice.entity.ScheduledCall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

/**
 * ScheduledCallRepository — Spring Data JPA repository for ScheduledCall entities.
 * Provides the background worker query for picking up due PENDING calls.
 */
@Repository
public interface ScheduledCallRepository extends JpaRepository<ScheduledCall, Long> {

    /**
     * Finds all calls with the given status whose scheduledAt time is before the cutoff.
     * Used by VoiceCallBackgroundWorker to discover due PENDING calls.
     *
     * @param status   call status filter (e.g. "PENDING")
     * @param cutoff   upper bound on scheduledAt (exclusive), typically Instant.now()
     * @return list of ScheduledCall records to process
     */
    List<ScheduledCall> findByStatusAndScheduledAtBefore(String status, Instant cutoff);
}
