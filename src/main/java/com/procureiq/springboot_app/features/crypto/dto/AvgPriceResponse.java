package com.procureiq.springboot_app.features.crypto.dto;

import java.math.BigDecimal;

public record AvgPriceResponse(
        String      symbol,
        int         mins,
        BigDecimal  price
) {}
