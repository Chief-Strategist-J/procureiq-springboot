package com.procureiq.springboot_app.features.githubactions.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

@Entity
@Table(name = "github_action_templates")
public class GithubActionTemplate {
    @Id
    private Long id;

    @Column(nullable = false)
    private String name = "";

    @Column(nullable = false)
    private String category = "";

    @Column(nullable = false)
    private String description = "";

    @Column(name = "cron_expression", nullable = false)
    private String cronExpression = "";

    @Column(name = "event_type", nullable = false, unique = true)
    private String eventType = "";

    @Column(name = "yaml_content", nullable = false, columnDefinition = "TEXT")
    private String yamlContent = "";

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    public GithubActionTemplate() {}

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCronExpression() { return cronExpression; }
    public void setCronExpression(String cronExpression) { this.cronExpression = cronExpression; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getYamlContent() { return yamlContent; }
    public void setYamlContent(String yamlContent) { this.yamlContent = yamlContent; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
