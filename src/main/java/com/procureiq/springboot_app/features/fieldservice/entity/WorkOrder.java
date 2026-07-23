package com.procureiq.springboot_app.features.fieldservice.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "work_orders")
public class WorkOrder {
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_work_order_id")
    private WorkOrder parentWorkOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id")
    private Case caseEntity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entitlement_id")
    private Entitlement entitlement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id")
    private Contact contact;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id")
    private Asset asset;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_type_id")
    private WorkType workType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "price_book_id")
    private PriceBook priceBook;

    @Column(nullable = false)
    private String status = "new";

    @Column(nullable = false, columnDefinition = "smallint")
    private Short priority = 3;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public WorkOrder() {}

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

    public WorkOrder getParentWorkOrder() { return parentWorkOrder; }
    public void setParentWorkOrder(WorkOrder parentWorkOrder) { this.parentWorkOrder = parentWorkOrder; }

    public Case getCaseEntity() { return caseEntity; }
    public void setCaseEntity(Case caseEntity) { this.caseEntity = caseEntity; }

    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }

    public Entitlement getEntitlement() { return entitlement; }
    public void setEntitlement(Entitlement entitlement) { this.entitlement = entitlement; }

    public Contact getContact() { return contact; }
    public void setContact(Contact contact) { this.contact = contact; }

    public Asset getAsset() { return asset; }
    public void setAsset(Asset asset) { this.asset = asset; }

    public WorkType getWorkType() { return workType; }
    public void setWorkType(WorkType workType) { this.workType = workType; }

    public PriceBook getPriceBook() { return priceBook; }
    public void setPriceBook(PriceBook priceBook) { this.priceBook = priceBook; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Short getPriority() { return priority; }
    public void setPriority(Short priority) { this.priority = priority; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
