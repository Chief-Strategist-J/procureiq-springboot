package com.procureiq.springboot_app.features.fieldservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "maintenance_assets")
@IdClass(MaintenanceAssetId.class)
public class MaintenanceAsset {
    @Id
    @Column(name = "maintenance_plan_id")
    private Long maintenancePlanId;

    @Id
    @Column(name = "asset_id")
    private Long assetId;

    public MaintenanceAsset() {}

    public MaintenanceAsset(Long maintenancePlanId, Long assetId) {
        this.maintenancePlanId = maintenancePlanId;
        this.assetId = assetId;
    }

    public Long getMaintenancePlanId() { return maintenancePlanId; }
    public void setMaintenancePlanId(Long maintenancePlanId) { this.maintenancePlanId = maintenancePlanId; }

    public Long getAssetId() { return assetId; }
    public void setAssetId(Long assetId) { this.assetId = assetId; }
}
