package com.procureiq.springboot_app.features.fieldservice.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ServiceResourceCapacityRequest(
    Long serviceResourceId,
    String capacityType,
    BigDecimal capacityValue,
    LocalDate startDate,
    LocalDate endDate
) {}
