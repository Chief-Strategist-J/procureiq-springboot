package com.procureiq.springboot_app.features.fieldservice.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ServiceResourceCapacityResponse(
    Long id,
    Long serviceResourceId,
    String capacityType,
    BigDecimal capacityValue,
    LocalDate startDate,
    LocalDate endDate
) {}
