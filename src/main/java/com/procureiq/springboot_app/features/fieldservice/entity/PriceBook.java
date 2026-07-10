package com.procureiq.springboot_app.features.fieldservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "price_books")
public class PriceBook {
    @Id
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "is_standard", nullable = false)
    private Boolean isStandard = false;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    public PriceBook() {}

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

    public Boolean getIsStandard() { return isStandard; }
    public void setIsStandard(Boolean standard) { isStandard = standard; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean active) { isActive = active; }
}
