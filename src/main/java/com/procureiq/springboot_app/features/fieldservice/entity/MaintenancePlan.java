package com.procureiq.springboot_app.features.fieldservice.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "maintenance_plans")
public class MaintenancePlan {
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id")
    private Asset asset;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_type_id")
    private WorkType workType;

    @Column(nullable = false)
    private String frequency;

    @Column(name = "generation_lead_days", nullable = false)
    private Integer generationLeadDays = 14;

    @Column(name = "next_generation_date", nullable = false)
    private LocalDate nextGenerationDate;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    public MaintenancePlan() {}

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = java.util.concurrent.ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }

    public Asset getAsset() { return asset; }
    public void setAsset(Asset asset) { this.asset = asset; }

    public WorkType getWorkType() { return workType; }
    public void setWorkType(WorkType workType) { this.workType = workType; }

    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    public Integer getGenerationLeadDays() { return generationLeadDays; }
    public void setGenerationLeadDays(Integer generationLeadDays) { this.generationLeadDays = generationLeadDays; }

    public LocalDate getNextGenerationDate() { return nextGenerationDate; }
    public void setNextGenerationDate(LocalDate nextGenerationDate) { this.nextGenerationDate = nextGenerationDate; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean active) { isActive = active; }
}
