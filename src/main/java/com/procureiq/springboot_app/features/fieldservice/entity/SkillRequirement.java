package com.procureiq.springboot_app.features.fieldservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "skill_requirements")
public class SkillRequirement {
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(name = "required_for_type", nullable = false)
    private String requiredForType;

    @Column(name = "required_for_id", nullable = false)
    private Long requiredForId;

    @Column(name = "min_skill_level", nullable = false, columnDefinition = "smallint")
    private Short minSkillLevel = 1;

    public SkillRequirement() {}

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = java.util.concurrent.ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Skill getSkill() { return skill; }
    public void setSkill(Skill skill) { this.skill = skill; }

    public String getRequiredForType() { return requiredForType; }
    public void setRequiredForType(String requiredForType) { this.requiredForType = requiredForType; }

    public Long getRequiredForId() { return requiredForId; }
    public void setRequiredForId(Long requiredForId) { this.requiredForId = requiredForId; }

    public Short getMinSkillLevel() { return minSkillLevel; }
    public void setMinSkillLevel(Short minSkillLevel) { this.minSkillLevel = minSkillLevel; }
}
