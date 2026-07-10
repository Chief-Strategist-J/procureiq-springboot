package com.procureiq.springboot_app.features.fieldservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "work_types")
public class WorkType {
    @Id
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "default_duration_minutes", nullable = false)
    private Integer defaultDurationMinutes = 60;

    @Column(name = "estimated_travel_minutes", nullable = false)
    private Integer estimatedTravelMinutes = 0;

    public WorkType() {}

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = java.util.concurrent.ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getDefaultDurationMinutes() { return defaultDurationMinutes; }
    public void setDefaultDurationMinutes(Integer defaultDurationMinutes) { this.defaultDurationMinutes = defaultDurationMinutes; }

    public Integer getEstimatedTravelMinutes() { return estimatedTravelMinutes; }
    public void setEstimatedTravelMinutes(Integer estimatedTravelMinutes) { this.estimatedTravelMinutes = estimatedTravelMinutes; }
}
