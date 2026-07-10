package com.procureiq.springboot_app.features.fieldservice.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "assigned_resources")
public class AssignedResource {
    @Id
    private Long id;

    @Column(name = "service_appointment_id", nullable = false)
    private Long serviceAppointmentId;

    @Column(name = "service_appointment_created_at", nullable = false)
    private Instant serviceAppointmentCreatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_resource_id")
    private ServiceResource serviceResource;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_crew_id")
    private ServiceCrew serviceCrew;

    @Column(name = "is_primary_resource", nullable = false)
    private Boolean isPrimaryResource = true;

    @Column(name = "assigned_at", nullable = false)
    private Instant assignedAt = Instant.now();

    @Column(nullable = false)
    private String status = "assigned";

    public AssignedResource() {}

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = java.util.concurrent.ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);
        }
        if (this.assignedAt == null) {
            this.assignedAt = Instant.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getServiceAppointmentId() { return serviceAppointmentId; }
    public void setServiceAppointmentId(Long serviceAppointmentId) { this.serviceAppointmentId = serviceAppointmentId; }

    public Instant getServiceAppointmentCreatedAt() { return serviceAppointmentCreatedAt; }
    public void setServiceAppointmentCreatedAt(Instant serviceAppointmentCreatedAt) { this.serviceAppointmentCreatedAt = serviceAppointmentCreatedAt; }

    public ServiceResource getServiceResource() { return serviceResource; }
    public void setServiceResource(ServiceResource serviceResource) { this.serviceResource = serviceResource; }

    public ServiceCrew getServiceCrew() { return serviceCrew; }
    public void setServiceCrew(ServiceCrew serviceCrew) { this.serviceCrew = serviceCrew; }

    public Boolean getIsPrimaryResource() { return isPrimaryResource; }
    public void setIsPrimaryResource(Boolean isPrimaryResource) { this.isPrimaryResource = isPrimaryResource; }

    public Instant getAssignedAt() { return assignedAt; }
    public void setAssignedAt(Instant assignedAt) { this.assignedAt = assignedAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
