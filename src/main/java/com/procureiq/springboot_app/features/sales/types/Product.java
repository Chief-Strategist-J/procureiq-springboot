package com.procureiq.springboot_app.features.sales.types;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "products", schema = "blute")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "productname", nullable = false, length = 250)
    private String productName;

    @Column(name = "description", length = 4000)
    private String description;

    @Column(name = "hsncode", length = 10)
    private String hsnCode;

    @Column(name = "mfrpartno", length = 30)
    private String mfrPartNo;

    @Column(name = "purchasecost")
    private Double purchaseCost;

    @Column(name = "unitrate")
    private Double unitRate;

    @Column(name = "isactive", nullable = false)
    private Boolean isActive;

    @Column(name = "isdeleted", nullable = false)
    private Boolean isDeleted;

    @Column(name = "entrydatetime", nullable = false)
    private LocalDateTime entryDateTime;

    @Column(name = "updateentrydatetime", nullable = false)
    private LocalDateTime updateEntryDateTime;

    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @Column(name = "updateusername", nullable = false, length = 100)
    private String updateUsername;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getHsnCode() { return hsnCode; }
    public void setHsnCode(String hsnCode) { this.hsnCode = hsnCode; }

    public String getMfrPartNo() { return mfrPartNo; }
    public void setMfrPartNo(String mfrPartNo) { this.mfrPartNo = mfrPartNo; }

    public Double getPurchaseCost() { return purchaseCost; }
    public void setPurchaseCost(Double purchaseCost) { this.purchaseCost = purchaseCost; }

    public Double getUnitRate() { return unitRate; }
    public void setUnitRate(Double unitRate) { this.unitRate = unitRate; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }

    public LocalDateTime getEntryDateTime() { return entryDateTime; }
    public void setEntryDateTime(LocalDateTime entryDateTime) { this.entryDateTime = entryDateTime; }

    public LocalDateTime getUpdateEntryDateTime() { return updateEntryDateTime; }
    public void setUpdateEntryDateTime(LocalDateTime updateEntryDateTime) { this.updateEntryDateTime = updateEntryDateTime; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getUpdateUsername() { return updateUsername; }
    public void setUpdateUsername(String updateUsername) { this.updateUsername = updateUsername; }
}
