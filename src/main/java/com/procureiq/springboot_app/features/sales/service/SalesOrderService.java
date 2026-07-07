package com.procureiq.springboot_app.features.sales.service;

import com.procureiq.springboot_app.features.sales.repository.AccountRepository;
import com.procureiq.springboot_app.features.sales.repository.ProductRepository;
import com.procureiq.springboot_app.features.sales.repository.SalesOrderRepository;
import com.procureiq.springboot_app.features.sales.types.Account;
import com.procureiq.springboot_app.features.sales.types.Product;
import com.procureiq.springboot_app.features.sales.types.SalesOrder;
import com.procureiq.springboot_app.features.sales.types.SalesOrderProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SalesOrderService {

    @Autowired
    private SalesOrderRepository salesOrderRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<SalesOrder> getAllSalesOrders() {
        return salesOrderRepository.findByIsDeletedFalse();
    }

    public Optional<SalesOrder> getSalesOrderById(Long id) {
        return salesOrderRepository.findById(id)
                .filter(so -> !so.getIsDeleted());
    }

    @Transactional
    public SalesOrder createSalesOrder(SalesOrder salesOrder) {
        if (salesOrder.getAccount() == null || salesOrder.getAccount().getId() == null) {
            throw new IllegalArgumentException("Account must be specified with a valid ID");
        }
        Account account = accountRepository.findById(salesOrder.getAccount().getId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found with ID: " + salesOrder.getAccount().getId()));
        salesOrder.setAccount(account);

        salesOrder.setEntryDateTime(LocalDateTime.now());
        salesOrder.setUpdateEntryDateTime(LocalDateTime.now());
        salesOrder.setIsActive(true);
        salesOrder.setIsDeleted(false);

        if (salesOrder.getOrderProducts() != null) {
            for (SalesOrderProduct sop : salesOrder.getOrderProducts()) {
                if (sop.getProduct() == null || sop.getProduct().getId() == null) {
                    throw new IllegalArgumentException("Every line item must reference a valid Product");
                }
                Product product = productRepository.findById(sop.getProduct().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + sop.getProduct().getId()));
                sop.setProduct(product);
                
                sop.setAmdNo(0L);
                sop.setOptions(0L);
                sop.setIsRemove(false);
                if (sop.getPurchaseRatePerQty() == null) {
                    sop.setPurchaseRatePerQty(product.getPurchaseCost() != null ? product.getPurchaseCost() : 0.0);
                }
            }
        }

        return salesOrderRepository.save(salesOrder);
    }

    @Transactional
    public SalesOrder updateSalesOrder(Long id, SalesOrder updatedOrder) {
        SalesOrder existingOrder = salesOrderRepository.findById(id)
                .filter(so -> !so.getIsDeleted())
                .orElseThrow(() -> new IllegalArgumentException("Sales Order not found with ID: " + id));

        existingOrder.setDeliveryDate(updatedOrder.getDeliveryDate());
        existingOrder.setDescription(updatedOrder.getDescription());
        existingOrder.setPayMode(updatedOrder.getPayMode());
        existingOrder.setPoNumber(updatedOrder.getPoNumber());
        existingOrder.setPoDate(updatedOrder.getPoDate());
        existingOrder.setSalesOrderDate(updatedOrder.getSalesOrderDate());
        existingOrder.setUpdateEntryDateTime(LocalDateTime.now());
        existingOrder.setUpdateUsername(updatedOrder.getUpdateUsername());

        if (updatedOrder.getAccount() != null && updatedOrder.getAccount().getId() != null) {
            Account account = accountRepository.findById(updatedOrder.getAccount().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Account not found with ID: " + updatedOrder.getAccount().getId()));
            existingOrder.setAccount(account);
        }

        if (updatedOrder.getOrderProducts() != null) {
            existingOrder.getOrderProducts().clear();
            for (SalesOrderProduct sop : updatedOrder.getOrderProducts()) {
                Product product = productRepository.findById(sop.getProduct().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + sop.getProduct().getId()));
                sop.setProduct(product);
                existingOrder.addProduct(sop);
            }
        }

        return salesOrderRepository.save(existingOrder);
    }

    @Transactional
    public void deleteSalesOrder(Long id) {
        SalesOrder salesOrder = salesOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sales Order not found with ID: " + id));
        salesOrder.setIsDeleted(true);
        salesOrderRepository.save(salesOrder);
    }
}
