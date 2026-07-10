package com.procureiq.springboot_app.features.fieldservice.repository;

import com.procureiq.springboot_app.features.fieldservice.entity.TravelTimeCache;
import com.procureiq.springboot_app.features.fieldservice.entity.TravelTimeCacheId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TravelTimeCacheRepository extends JpaRepository<TravelTimeCache, TravelTimeCacheId> {
}
