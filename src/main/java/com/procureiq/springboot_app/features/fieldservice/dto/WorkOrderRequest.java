package com.procureiq.springboot_app.features.fieldservice.dto;

public record WorkOrderRequest(
    Long parentWorkOrderId,
    Long caseId,
    Long accountId,
    Long entitlementId,
    Long contactId,
    Long assetId,
    Long workTypeId,
    Long priceBookId,
    String status,
    Integer priority
) {}
