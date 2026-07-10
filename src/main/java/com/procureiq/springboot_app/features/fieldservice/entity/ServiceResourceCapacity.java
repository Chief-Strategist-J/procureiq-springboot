package com.procureiq.springboot_app.features.fieldservice.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "service_resource_capacities")
public class ServiceResourceCapacity {
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_resource_id", nullable = false)
    private ServiceResource serviceResource;

    @Column(name = "capacity_type", nullable = false)
    private String capacityType;

    @Column(name = "capacity_value", nullable = false, precision = 8, scale = 2)
    private BigDecimal capacityValue;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    public ServiceResourceCapacity() {}

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

    public String getCapacityType() { return capacityType; }
    public void setCapacityType(String capacityType) { this.capacityType = capacityType; }

    public BigDecimal getCapacityValue() { return capacityValue; }
    public void setCapacityValue(BigDecimal capacityValue) { this.capacityValue = capacityValue; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
}
