package com.procureiq.springboot_app.features.fieldservice.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Map;
import com.procureiq.springboot_app.shared.utils.JsonMapConverter;

@Entity
@Table(name = "service_appointments")
@IdClass(ServiceAppointmentId.class)
public class ServiceAppointment {
    @Id
    private Long id;

    @Column(name = "parent_record_type", nullable = false)
    private String parentRecordType;

    @Column(name = "parent_record_id", nullable = false)
    private Long parentRecordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id")
    private Contact contact;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_territory_id")
    private ServiceTerritory serviceTerritory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_type_id")
    private WorkType workType;

    @Column(nullable = false)
    private String status = "none";

    @Column(name = "scheduled_start")
    private Instant scheduledStart;

    @Column(name = "scheduled_end")
    private Instant scheduledEnd;

    @Column(name = "arrival_window_start")
    private Instant arrivalWindowStart;

    @Column(name = "arrival_window_end")
    private Instant arrivalWindowEnd;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Convert(converter = JsonMapConverter.class)
    @Column(name = "address", columnDefinition = "jsonb")
    private Map<String, Object> address;

    @Id
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public ServiceAppointment() {}

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = java.util.concurrent.ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);
        }
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getParentRecordType() { return parentRecordType; }
    public void setParentRecordType(String parentRecordType) { this.parentRecordType = parentRecordType; }

    public Long getParentRecordId() { return parentRecordId; }
    public void setParentRecordId(Long parentRecordId) { this.parentRecordId = parentRecordId; }

    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }

    public Contact getContact() { return contact; }
    public void setContact(Contact contact) { this.contact = contact; }

    public ServiceTerritory getServiceTerritory() { return serviceTerritory; }
    public void setServiceTerritory(ServiceTerritory serviceTerritory) { this.serviceTerritory = serviceTerritory; }

    public WorkType getWorkType() { return workType; }
    public void setWorkType(WorkType workType) { this.workType = workType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Instant getScheduledStart() { return scheduledStart; }
    public void setScheduledStart(Instant scheduledStart) { this.scheduledStart = scheduledStart; }

    public Instant getScheduledEnd() { return scheduledEnd; }
    public void setScheduledEnd(Instant scheduledEnd) { this.scheduledEnd = scheduledEnd; }

    public Instant getArrivalWindowStart() { return arrivalWindowStart; }
    public void setArrivalWindowStart(Instant arrivalWindowStart) { this.arrivalWindowStart = arrivalWindowStart; }

    public Instant getArrivalWindowEnd() { return arrivalWindowEnd; }
    public void setArrivalWindowEnd(Instant arrivalWindowEnd) { this.arrivalWindowEnd = arrivalWindowEnd; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public Map<String, Object> getAddress() { return address; }
    public void setAddress(Map<String, Object> address) { this.address = address; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
