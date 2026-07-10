package com.procureiq.springboot_app.features.fieldservice.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "service_resource_skills", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"service_resource_id", "skill_id"})
})
public class ServiceResourceSkill {
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_resource_id", nullable = false)
    private ServiceResource serviceResource;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(name = "skill_level", nullable = false)
    private Integer skillLevel = 1;

    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom = LocalDate.now();

    @Column(name = "valid_to")
    private LocalDate validTo;

    public ServiceResourceSkill() {}

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

    public Skill getSkill() { return skill; }
    public void setSkill(Skill skill) { this.skill = skill; }

    public Integer getSkillLevel() { return skillLevel; }
    public void setSkillLevel(Integer skillLevel) { this.skillLevel = skillLevel; }

    public LocalDate getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDate validFrom) { this.validFrom = validFrom; }

    public LocalDate getValidTo() { return validTo; }
    public void setValidTo(LocalDate validTo) { this.validTo = validTo; }
}
