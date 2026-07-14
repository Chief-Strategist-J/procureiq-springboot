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
) {}
