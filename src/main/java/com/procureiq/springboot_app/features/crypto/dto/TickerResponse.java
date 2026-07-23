package com.procureiq.springboot_app.features.crypto.dto;

import java.math.BigDecimal;

public record TickerResponse(
        String symbol,
        BigDecimal price,
        BigDecimal priceChangePercent,
        BigDecimal highPrice,
        BigDecimal lowPrice,
        BigDecimal volume
) {}
