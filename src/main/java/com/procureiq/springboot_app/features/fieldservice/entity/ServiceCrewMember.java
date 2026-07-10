package com.procureiq.springboot_app.features.fieldservice.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "service_crew_members", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"service_crew_id", "service_resource_id", "start_date"})
})
public class ServiceCrewMember {
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_crew_id", nullable = false)
    private ServiceCrew serviceCrew;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_resource_id", nullable = false)
    private ServiceResource serviceResource;

    @Column(name = "member_role", nullable = false)
    private String memberRole = "member";

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate = LocalDate.now();

    @Column(name = "end_date")
    private LocalDate endDate;

    public ServiceCrewMember() {}

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = java.util.concurrent.ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ServiceCrew getServiceCrew() { return serviceCrew; }
    public void setServiceCrew(ServiceCrew serviceCrew) { this.serviceCrew = serviceCrew; }

    public ServiceResource getServiceResource() { return serviceResource; }
    public void setServiceResource(ServiceResource serviceResource) { this.serviceResource = serviceResource; }

    public String getMemberRole() { return memberRole; }
    public void setMemberRole(String memberRole) { this.memberRole = memberRole; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
}
