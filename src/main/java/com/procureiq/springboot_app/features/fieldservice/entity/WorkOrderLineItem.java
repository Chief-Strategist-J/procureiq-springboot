package com.procureiq.springboot_app.features.fieldservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "work_order_line_items")
public class WorkOrderLineItem {
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_line_item_id")
    private WorkOrderLineItem parentLineItem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "work_order_id", nullable = false)
    private WorkOrder workOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id")
    private Asset asset;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_type_id")
    private WorkType workType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "price_book_entry_id")
    private PriceBookEntry priceBookEntry;

    @Column(nullable = false)
    private String status = "new";

    public WorkOrderLineItem() {}

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = java.util.concurrent.ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public WorkOrderLineItem getParentLineItem() { return parentLineItem; }
    public void setParentLineItem(WorkOrderLineItem parentLineItem) { this.parentLineItem = parentLineItem; }

    public WorkOrder getWorkOrder() { return workOrder; }
    public void setWorkOrder(WorkOrder workOrder) { this.workOrder = workOrder; }

    public Asset getAsset() { return asset; }
    public void setAsset(Asset asset) { this.asset = asset; }

    public WorkType getWorkType() { return workType; }
    public void setWorkType(WorkType workType) { this.workType = workType; }

    public PriceBookEntry getPriceBookEntry() { return priceBookEntry; }
    public void setPriceBookEntry(PriceBookEntry priceBookEntry) { this.priceBookEntry = priceBookEntry; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
