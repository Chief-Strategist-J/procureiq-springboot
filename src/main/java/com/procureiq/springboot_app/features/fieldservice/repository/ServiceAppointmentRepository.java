package com.procureiq.springboot_app.features.fieldservice.repository;

import com.procureiq.springboot_app.features.fieldservice.entity.ServiceAppointment;
import com.procureiq.springboot_app.features.fieldservice.entity.ServiceAppointmentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ServiceAppointmentRepository extends JpaRepository<ServiceAppointment, ServiceAppointmentId> {
    Optional<ServiceAppointment> findFirstById(Long id);
}

