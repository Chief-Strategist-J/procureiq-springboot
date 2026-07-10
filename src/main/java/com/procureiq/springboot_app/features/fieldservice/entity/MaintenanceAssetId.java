package com.procureiq.springboot_app.features.fieldservice.entity;

import java.io.Serializable;
import java.util.Objects;

public class MaintenanceAssetId implements Serializable {
    private Long maintenancePlanId;
    private Long assetId;

    public MaintenanceAssetId() {}

    public MaintenanceAssetId(Long maintenancePlanId, Long assetId) {
        this.maintenancePlanId = maintenancePlanId;
        this.assetId = assetId;
    }

    public Long getMaintenancePlanId() { return maintenancePlanId; }
    public void setMaintenancePlanId(Long maintenancePlanId) { this.maintenancePlanId = maintenancePlanId; }

    public Long getAssetId() { return assetId; }
    public void setAssetId(Long assetId) { this.assetId = assetId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MaintenanceAssetId that = (MaintenanceAssetId) o;
        return Objects.equals(maintenancePlanId, that.maintenancePlanId) &&
               Objects.equals(assetId, that.assetId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maintenancePlanId, assetId);
    }
}
