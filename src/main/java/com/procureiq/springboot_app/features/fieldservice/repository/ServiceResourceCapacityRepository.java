package com.procureiq.springboot_app.features.fieldservice.repository;

import com.procureiq.springboot_app.features.fieldservice.entity.ServiceResourceCapacity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceResourceCapacityRepository extends JpaRepository<ServiceResourceCapacity, Long> {
}
