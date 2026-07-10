package com.procureiq.springboot_app.features.fieldservice.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "travel_time_cache")
@IdClass(TravelTimeCacheId.class)
public class TravelTimeCache {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_territory_id")
    private ServiceTerritory originTerritory;

    @Id
    @Column(name = "dest_lat_bucket")
    private Integer destLatBucket;

    @Id
    @Column(name = "dest_lng_bucket")
    private Integer destLngBucket;

    @Column(name = "estimated_minutes", nullable = false)
    private Integer estimatedMinutes;

    @Column(name = "computed_at", nullable = false)
    private Instant computedAt = Instant.now();

    public TravelTimeCache() {}

    @PrePersist
    protected void onCreate() {
        if (this.computedAt == null) {
            this.computedAt = Instant.now();
        }
    }

    public ServiceTerritory getOriginTerritory() { return originTerritory; }
    public void setOriginTerritory(ServiceTerritory originTerritory) { this.originTerritory = originTerritory; }

    public Integer getDestLatBucket() { return destLatBucket; }
    public void setDestLatBucket(Integer destLatBucket) { this.destLatBucket = destLatBucket; }

    public Integer getDestLngBucket() { return destLngBucket; }
    public void setDestLngBucket(Integer destLngBucket) { this.destLngBucket = destLngBucket; }

    public Integer getEstimatedMinutes() { return estimatedMinutes; }
    public void setEstimatedMinutes(Integer estimatedMinutes) { this.estimatedMinutes = estimatedMinutes; }

    public Instant getComputedAt() { return computedAt; }
    public void setComputedAt(Instant computedAt) { this.computedAt = computedAt; }
}
