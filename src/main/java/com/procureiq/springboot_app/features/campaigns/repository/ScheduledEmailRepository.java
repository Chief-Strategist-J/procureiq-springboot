package com.procureiq.springboot_app.features.campaigns.repository;

import com.procureiq.springboot_app.features.campaigns.entity.ScheduledEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduledEmailRepository extends JpaRepository<ScheduledEmail, Long> {
}
