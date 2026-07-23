package com.procureiq.springboot_app.features.crypto.dto;

import java.math.BigDecimal;

public record BookTickerResponse(
        String      symbol,
        BigDecimal  bidPrice,
        BigDecimal  bidQty,
        BigDecimal  askPrice,
        BigDecimal  askQty
) {}
