package com.procureiq.springboot_app.features.sales;

import com.procureiq.springboot_app.features.sales.service.SalesOrderService;
import com.procureiq.springboot_app.features.sales.types.SalesOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component
public class SalesFeature {

    @Autowired
    private SalesOrderService salesOrderService;

    public List<SalesOrder> getAllSalesOrders() {
        return salesOrderService.getAllSalesOrders();
    }

    public Optional<SalesOrder> getSalesOrderById(Long id) {
        return salesOrderService.getSalesOrderById(id);
    }

    public SalesOrder createSalesOrder(SalesOrder salesOrder) {
        return salesOrderService.createSalesOrder(salesOrder);
    }

    public SalesOrder updateSalesOrder(Long id, SalesOrder salesOrder) {
        return salesOrderService.updateSalesOrder(id, salesOrder);
    }

    public void deleteSalesOrder(Long id) {
        salesOrderService.deleteSalesOrder(id);
    }
}
