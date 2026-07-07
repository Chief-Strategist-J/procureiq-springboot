package com.procureiq.springboot_app.features.sales.types;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sales_order", schema = "blute")
public class SalesOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "amdno", nullable = false)
    private Long amdNo = 0L;

    @Column(name = "assignall", nullable = false)
    private Boolean assignAll = false;

    @Column(name = "deliverydate", nullable = false)
    private LocalDate deliveryDate;

    @Column(name = "description", length = 4000)
    private String description;

    @Column(name = "entrydatetime", nullable = false)
    private LocalDateTime entryDateTime;

    @Column(name = "idval", nullable = false)
    private Long idVal = 0L;

    @Column(name = "isactive", nullable = false)
    private Boolean isActive = true;

    @Column(name = "isdeleted", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "pay_mode", length = 90)
    private String payMode;

    @Column(name = "ponumber", length = 90)
    private String poNumber;

    @Column(name = "podate")
    private LocalDate poDate;

    @Column(name = "salesorderdate", nullable = false)
    private LocalDate salesOrderDate;

    @Column(name = "updateentrydatetime", nullable = false)
    private LocalDateTime updateEntryDateTime;

    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @Column(name = "updateusername", nullable = false, length = 100)
    private String updateUsername;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "leadacccon_id", nullable = false)
    private Account account;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "ids")
    private List<SalesOrderProduct> orderProducts = new ArrayList<>();

    public void addProduct(SalesOrderProduct product) {
        orderProducts.add(product);
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAmdNo() { return amdNo; }
    public void setAmdNo(Long amdNo) { this.amdNo = amdNo; }

    public Boolean getAssignAll() { return assignAll; }
    public void setAssignAll(Boolean assignAll) { this.assignAll = assignAll; }

    public LocalDate getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(LocalDate deliveryDate) { this.deliveryDate = deliveryDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getEntryDateTime() { return entryDateTime; }
    public void setEntryDateTime(LocalDateTime entryDateTime) { this.entryDateTime = entryDateTime; }

    public Long getIdVal() { return idVal; }
    public void setIdVal(Long idVal) { this.idVal = idVal; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }

    public String getPayMode() { return payMode; }
    public void setPayMode(String payMode) { this.payMode = payMode; }

    public String getPoNumber() { return poNumber; }
    public void setPoNumber(String poNumber) { this.poNumber = poNumber; }

    public LocalDate getPoDate() { return poDate; }
    public void setPoDate(LocalDate poDate) { this.poDate = poDate; }

    public LocalDate getSalesOrderDate() { return salesOrderDate; }
    public void setSalesOrderDate(LocalDate salesOrderDate) { this.salesOrderDate = salesOrderDate; }

    public LocalDateTime getUpdateEntryDateTime() { return updateEntryDateTime; }
    public void setUpdateEntryDateTime(LocalDateTime updateEntryDateTime) { this.updateEntryDateTime = updateEntryDateTime; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getUpdateUsername() { return updateUsername; }
    public void setUpdateUsername(String updateUsername) { this.updateUsername = updateUsername; }

    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }

    public List<SalesOrderProduct> getOrderProducts() { return orderProducts; }
    public void setOrderProducts(List<SalesOrderProduct> orderProducts) { this.orderProducts = orderProducts; }
}
