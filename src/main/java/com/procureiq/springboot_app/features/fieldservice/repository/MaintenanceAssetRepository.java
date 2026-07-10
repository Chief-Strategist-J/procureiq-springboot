package com.procureiq.springboot_app.features.fieldservice.repository;

import com.procureiq.springboot_app.features.fieldservice.entity.MaintenanceAsset;
import com.procureiq.springboot_app.features.fieldservice.entity.MaintenanceAssetId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaintenanceAssetRepository extends JpaRepository<MaintenanceAsset, MaintenanceAssetId> {
}
