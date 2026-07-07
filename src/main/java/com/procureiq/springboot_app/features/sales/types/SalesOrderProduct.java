package com.procureiq.springboot_app.features.sales.types;

import jakarta.persistence.*;

@Entity
@Table(name = "map_sales_order_products", schema = "blute")
public class SalesOrderProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ids")
    private Long salesOrderId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "productsid_id", nullable = false)
    private Product product;

    @Column(name = "qty", nullable = false)
    private Float qty;

    @Column(name = "rateperqty", nullable = false)
    private Double ratePerQty;

    @Column(name = "purrateperqty", nullable = false)
    private Double purchaseRatePerQty = 0.0;

    @Column(name = "taxper", nullable = false)
    private Float taxPer;

    @Column(name = "discval")
    private Double discountValue;

    @Column(name = "discvaltype", length = 90)
    private String discountValueType;

    @Column(name = "productinfo", length = 4000)
    private String productInfo;

    @Column(name = "isremove", nullable = false)
    private Boolean isRemove = false;

    @Column(name = "options", nullable = false)
    private Long options = 0L;

    @Column(name = "amdno", nullable = false)
    private Long amdNo = 0L;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSalesOrderId() { return salesOrderId; }
    public void setSalesOrderId(Long salesOrderId) { this.salesOrderId = salesOrderId; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Float getQty() { return qty; }
    public void setQty(Float qty) { this.qty = qty; }

    public Double getRatePerQty() { return ratePerQty; }
    public void setRatePerQty(Double ratePerQty) { this.ratePerQty = ratePerQty; }

    public Double getPurchaseRatePerQty() { return purchaseRatePerQty; }
    public void setPurchaseRatePerQty(Double purchaseRatePerQty) { this.purchaseRatePerQty = purchaseRatePerQty; }

    public Float getTaxPer() { return taxPer; }
    public void setTaxPer(Float taxPer) { this.taxPer = taxPer; }

    public Double getDiscountValue() { return discountValue; }
    public void setDiscountValue(Double discountValue) { this.discountValue = discountValue; }

    public String getDiscountValueType() { return discountValueType; }
    public void setDiscountValueType(String discountValueType) { this.discountValueType = discountValueType; }

    public String getProductInfo() { return productInfo; }
    public void setProductInfo(String productInfo) { this.productInfo = productInfo; }

    public Boolean getIsRemove() { return isRemove; }
    public void setIsRemove(Boolean isRemove) { this.isRemove = isRemove; }

    public Long getOptions() { return options; }
    public void setOptions(Long options) { this.options = options; }

    public Long getAmdNo() { return amdNo; }
    public void setAmdNo(Long amdNo) { this.amdNo = amdNo; }
}
