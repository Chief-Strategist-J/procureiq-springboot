package com.procureiq.springboot_app.features.fieldservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "milestones")
public class Milestone {
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "entitlement_process_id", nullable = false)
    private EntitlementProcess entitlementProcess;

    @Column(nullable = false)
    private String name;

    @Column(name = "target_minutes", nullable = false)
    private Integer targetMinutes;

    @Column(nullable = false)
    private Integer sequence;

    public Milestone() {}

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = java.util.concurrent.ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public EntitlementProcess getEntitlementProcess() { return entitlementProcess; }
    public void setEntitlementProcess(EntitlementProcess entitlementProcess) { this.entitlementProcess = entitlementProcess; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getTargetMinutes() { return targetMinutes; }
    public void setTargetMinutes(Integer targetMinutes) { this.targetMinutes = targetMinutes; }

    public Integer getSequence() { return sequence; }
    public void setSequence(Integer sequence) { this.sequence = sequence; }
}
