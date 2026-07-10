package com.procureiq.springboot_app.features.fieldservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "asset_relationships", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"asset_id", "related_asset_id", "relationship_type"})
})
public class AssetRelationship {
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "related_asset_id", nullable = false)
    private Asset relatedAsset;

    @Column(name = "relationship_type", nullable = false)
    private String relationshipType;

    public AssetRelationship() {}

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = java.util.concurrent.ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Asset getAsset() { return asset; }
    public void setAsset(Asset asset) { this.asset = asset; }

    public Asset getRelatedAsset() { return relatedAsset; }
    public void setRelatedAsset(Asset relatedAsset) { this.relatedAsset = relatedAsset; }

    public String getRelationshipType() { return relationshipType; }
    public void setRelationshipType(String relationshipType) { this.relationshipType = relationshipType; }
}
