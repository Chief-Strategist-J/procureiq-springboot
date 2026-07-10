package com.procureiq.springboot_app.features.fieldservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "service_territory_members", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"service_territory_id", "service_resource_id"})
})
public class ServiceTerritoryMember {
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_territory_id", nullable = false)
    private ServiceTerritory serviceTerritory;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_resource_id", nullable = false)
    private ServiceResource serviceResource;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operating_hours_id")
    private OperatingHours operatingHours;

    @Column(name = "territory_type", nullable = false)
    private String territoryType = "primary";

    @Column(name = "travel_mode", nullable = false)
    private String travelMode = "driving";

    public ServiceTerritoryMember() {}

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = java.util.concurrent.ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ServiceTerritory getServiceTerritory() { return serviceTerritory; }
    public void setServiceTerritory(ServiceTerritory serviceTerritory) { this.serviceTerritory = serviceTerritory; }

    public ServiceResource getServiceResource() { return serviceResource; }
    public void setServiceResource(ServiceResource serviceResource) { this.serviceResource = serviceResource; }

    public OperatingHours getOperatingHours() { return operatingHours; }
    public void setOperatingHours(OperatingHours operatingHours) { this.operatingHours = operatingHours; }

    public String getTerritoryType() { return territoryType; }
    public void setTerritoryType(String territoryType) { this.territoryType = territoryType; }

    public String getTravelMode() { return travelMode; }
    public void setTravelMode(String travelMode) { this.travelMode = travelMode; }
}
