package com.procureiq.springboot_app.features.fieldservice.dto.response;

import java.time.Instant;

public record WorkOrderResponse(
    Long id,
    Long parentWorkOrderId,
    Long caseId,
    Long accountId,
    Long entitlementId,
    Long contactId,
    Long assetId,
    Long workTypeId,
    Long priceBookId,
    String status,
    Integer priority,
    Instant createdAt
) {
    public static WorkOrderResponse fromEntity(final com.procureiq.springboot_app.features.fieldservice.entity.WorkOrder entity) {
        return new WorkOrderResponse(
            entity.getId(),
                    entity.getParentWorkOrder() != null ? entity.getParentWorkOrder().getId() : null,
                    entity.getCaseEntity() != null ? entity.getCaseEntity().getId() : null,
                    entity.getAccount().getId(),
                    entity.getEntitlement() != null ? entity.getEntitlement().getId() : null,
                    entity.getContact() != null ? entity.getContact().getId() : null,
                    entity.getAsset() != null ? entity.getAsset().getId() : null,
                    entity.getWorkType() != null ? entity.getWorkType().getId() : null,
                    entity.getPriceBook() != null ? entity.getPriceBook().getId() : null,
                    entity.getStatus(),
                    entity.getPriority() != null ? entity.getPriority().intValue() : null,
                    entity.getCreatedAt()
        );
    }
}
