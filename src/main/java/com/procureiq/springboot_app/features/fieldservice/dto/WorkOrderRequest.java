package com.procureiq.springboot_app.features.fieldservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record WorkOrderRequest(
    Long parentWorkOrderId,
    Long caseId,
    Long accountId,
    Long entitlementId,
    Long contactId,
    Long assetId,
    @NotNull(message = "Work Type ID is required")
    Long workTypeId,
    Long priceBookId,
    @NotBlank(message = "Status is required")
    String status,
    Integer priority
) {}
