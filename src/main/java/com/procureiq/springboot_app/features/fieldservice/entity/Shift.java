package com.procureiq.springboot_app.features.fieldservice.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "shifts")
public class Shift {
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_resource_id", nullable = false)
    private ServiceResource serviceResource;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_territory_id", nullable = false)
    private ServiceTerritory serviceTerritory;

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    @Column(name = "shift_type", nullable = false)
    private String shiftType = "standard";

    public Shift() {}

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = java.util.concurrent.ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ServiceResource getServiceResource() { return serviceResource; }
    public void setServiceResource(ServiceResource serviceResource) { this.serviceResource = serviceResource; }

    public ServiceTerritory getServiceTerritory() { return serviceTerritory; }
    public void setServiceTerritory(ServiceTerritory serviceTerritory) { this.serviceTerritory = serviceTerritory; }

    public Instant getStartTime() { return startTime; }
    public void setStartTime(Instant startTime) { this.startTime = startTime; }

    public Instant getEndTime() { return endTime; }
    public void setEndTime(Instant endTime) { this.endTime = endTime; }

    public String getShiftType() { return shiftType; }
    public void setShiftType(String shiftType) { this.shiftType = shiftType; }
}
