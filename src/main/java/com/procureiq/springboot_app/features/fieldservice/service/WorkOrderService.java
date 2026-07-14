package com.procureiq.springboot_app.features.fieldservice.service;

import com.procureiq.springboot_app.features.fieldservice.dto.request.WorkOrderRequest;
import com.procureiq.springboot_app.features.fieldservice.dto.response.WorkOrderResponse;
import com.procureiq.springboot_app.features.fieldservice.entity.Account;
import com.procureiq.springboot_app.features.fieldservice.entity.ServiceAppointment;
import com.procureiq.springboot_app.features.fieldservice.entity.WorkOrder;
import com.procureiq.springboot_app.features.fieldservice.repository.*;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WorkOrderService {

    @Autowired
    private WorkOrderRepository workOrderRepository;
    @Autowired
    private CaseRepository caseRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private EntitlementRepository entitlementRepository;
    @Autowired
    private ContactRepository contactRepository;
    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private WorkTypeRepository workTypeRepository;
    @Autowired
    private PriceBookRepository priceBookRepository;
    @Autowired
    private ServiceAppointmentRepository serviceAppointmentRepository;

    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public WorkOrderService() {}

    @Transactional(readOnly = true)
    public List<WorkOrderResponse> getAllWorkOrders() {
        Span span = tracer.spanBuilder("WorkOrderService.getAllWorkOrders").startSpan();
        try {
            return workOrderRepository.findAll().stream()
                    .map(wo -> new WorkOrderResponse(
                            wo.getId(),
                            wo.getParentWorkOrder() != null ? wo.getParentWorkOrder().getId() : null,
                            wo.getCaseEntity() != null ? wo.getCaseEntity().getId() : null,
                            wo.getAccount().getId(),
                            wo.getEntitlement() != null ? wo.getEntitlement().getId() : null,
                            wo.getContact() != null ? wo.getContact().getId() : null,
                            wo.getAsset() != null ? wo.getAsset().getId() : null,
                            wo.getWorkType() != null ? wo.getWorkType().getId() : null,
                            wo.getPriceBook() != null ? wo.getPriceBook().getId() : null,
                            wo.getStatus(),
                            wo.getPriority(),
                            wo.getCreatedAt()
                    ))
                    .toList();
        } finally {
            span.end();
        }
    }

    @Transactional
    public WorkOrderResponse createWorkOrder(WorkOrderRequest request) {
        Span span = tracer.spanBuilder("WorkOrderService.createWorkOrder").startSpan();
        try {
            WorkOrder wo = new WorkOrder();
            if (request.parentWorkOrderId() != null) {
                wo.setParentWorkOrder(workOrderRepository.findById(request.parentWorkOrderId()).orElse(null));
            }
            if (request.caseId() != null) {
                wo.setCaseEntity(caseRepository.findById(request.caseId()).orElse(null));
            }

            Account account = null;
            if (request.accountId() != null) {
                account = accountRepository.findById(request.accountId()).orElse(null);
            }
            if (account == null) {
                account = new Account();
                account.setName("Default Account");
                account = accountRepository.save(account);
            }
            wo.setAccount(account);

            if (request.entitlementId() != null) {
                wo.setEntitlement(entitlementRepository.findById(request.entitlementId()).orElse(null));
            }
            if (request.contactId() != null) {
                wo.setContact(contactRepository.findById(request.contactId()).orElse(null));
            }
            if (request.assetId() != null) {
                wo.setAsset(assetRepository.findById(request.assetId()).orElse(null));
            }
            if (request.workTypeId() != null) {
                wo.setWorkType(workTypeRepository.findById(request.workTypeId()).orElse(null));
            }
            if (request.priceBookId() != null) {
                wo.setPriceBook(priceBookRepository.findById(request.priceBookId()).orElse(null));
            }
            if (request.status() != null) {
                wo.setStatus(request.status());
            }
            if (request.priority() != null) {
                wo.setPriority(request.priority());
            }

            wo = workOrderRepository.save(wo);

            
            if (wo.getWorkType() != null) {
                ServiceAppointment sa = new ServiceAppointment();
                sa.setParentRecordType("work_order");
                sa.setParentRecordId(wo.getId());
                sa.setStatus("none");
                sa.setDurationMinutes(wo.getWorkType().getDefaultDurationMinutes());
                sa.setAccount(wo.getAccount());
                sa.setContact(wo.getContact());
                sa.setWorkType(wo.getWorkType());
                serviceAppointmentRepository.save(sa);
            }

            return new WorkOrderResponse(
                    wo.getId(),
                    wo.getParentWorkOrder() != null ? wo.getParentWorkOrder().getId() : null,
                    wo.getCaseEntity() != null ? wo.getCaseEntity().getId() : null,
                    wo.getAccount().getId(),
                    wo.getEntitlement() != null ? wo.getEntitlement().getId() : null,
                    wo.getContact() != null ? wo.getContact().getId() : null,
                    wo.getAsset() != null ? wo.getAsset().getId() : null,
                    wo.getWorkType() != null ? wo.getWorkType().getId() : null,
                    wo.getPriceBook() != null ? wo.getPriceBook().getId() : null,
                    wo.getStatus(),
                    wo.getPriority(),
                    wo.getCreatedAt()
            );
        } finally {
            span.end();
        }
    }

    @Transactional(readOnly = true)
    public WorkOrderResponse getWorkOrder(Long id) {
        Span span = tracer.spanBuilder("WorkOrderService.getWorkOrder").startSpan();
        try {
            WorkOrder wo = workOrderRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("WorkOrder not found: " + id));
            return new WorkOrderResponse(
                    wo.getId(),
                    wo.getParentWorkOrder() != null ? wo.getParentWorkOrder().getId() : null,
                    wo.getCaseEntity() != null ? wo.getCaseEntity().getId() : null,
                    wo.getAccount().getId(),
                    wo.getEntitlement() != null ? wo.getEntitlement().getId() : null,
                    wo.getContact() != null ? wo.getContact().getId() : null,
                    wo.getAsset() != null ? wo.getAsset().getId() : null,
                    wo.getWorkType() != null ? wo.getWorkType().getId() : null,
                    wo.getPriceBook() != null ? wo.getPriceBook().getId() : null,
                    wo.getStatus(),
                    wo.getPriority(),
                    wo.getCreatedAt()
            );
        } finally {
            span.end();
        }
    }

    @Transactional
    public WorkOrderResponse updateWorkOrder(Long id, WorkOrderRequest request) {
        Span span = tracer.spanBuilder("WorkOrderService.updateWorkOrder").startSpan();
        try {
            WorkOrder wo = workOrderRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("WorkOrder not found: " + id));
            if (request.parentWorkOrderId() != null) {
                wo.setParentWorkOrder(workOrderRepository.findById(request.parentWorkOrderId()).orElse(null));
            } else {
                wo.setParentWorkOrder(null);
            }
            if (request.caseId() != null) {
                wo.setCaseEntity(caseRepository.findById(request.caseId()).orElse(null));
            } else {
                wo.setCaseEntity(null);
            }
            if (request.accountId() != null) {
                wo.setAccount(accountRepository.findById(request.accountId()).orElse(wo.getAccount()));
            }
            if (request.entitlementId() != null) {
                wo.setEntitlement(entitlementRepository.findById(request.entitlementId()).orElse(null));
            } else {
                wo.setEntitlement(null);
            }
            if (request.contactId() != null) {
                wo.setContact(contactRepository.findById(request.contactId()).orElse(null));
            } else {
                wo.setContact(null);
            }
            if (request.assetId() != null) {
                wo.setAsset(assetRepository.findById(request.assetId()).orElse(null));
            } else {
                wo.setAsset(null);
            }
            if (request.workTypeId() != null) {
                wo.setWorkType(workTypeRepository.findById(request.workTypeId()).orElse(null));
            } else {
                wo.setWorkType(null);
            }
            if (request.priceBookId() != null) {
                wo.setPriceBook(priceBookRepository.findById(request.priceBookId()).orElse(null));
            } else {
                wo.setPriceBook(null);
            }
            if (request.status() != null) {
                wo.setStatus(request.status());
            }
            if (request.priority() != null) {
                wo.setPriority(request.priority());
            }
            wo = workOrderRepository.save(wo);
            return new WorkOrderResponse(
                    wo.getId(),
                    wo.getParentWorkOrder() != null ? wo.getParentWorkOrder().getId() : null,
                    wo.getCaseEntity() != null ? wo.getCaseEntity().getId() : null,
                    wo.getAccount().getId(),
                    wo.getEntitlement() != null ? wo.getEntitlement().getId() : null,
                    wo.getContact() != null ? wo.getContact().getId() : null,
                    wo.getAsset() != null ? wo.getAsset().getId() : null,
                    wo.getWorkType() != null ? wo.getWorkType().getId() : null,
                    wo.getPriceBook() != null ? wo.getPriceBook().getId() : null,
                    wo.getStatus(),
                    wo.getPriority(),
                    wo.getCreatedAt()
            );
        } finally {
            span.end();
        }
    }

    @Transactional
    public void deleteWorkOrder(Long id) {
        Span span = tracer.spanBuilder("WorkOrderService.deleteWorkOrder").startSpan();
        try {
            workOrderRepository.deleteById(id);
        } finally {
            span.end();
        }
    }
}
