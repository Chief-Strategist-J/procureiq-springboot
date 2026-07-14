package com.procureiq.springboot_app.features.voice.repository;

import com.procureiq.springboot_app.features.voice.entity.ScheduledCall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;


@Repository
public interface ScheduledCallRepository extends JpaRepository<ScheduledCall, Long> {

    
    List<ScheduledCall> findByStatusAndScheduledAtBefore(String status, Instant cutoff);
}
