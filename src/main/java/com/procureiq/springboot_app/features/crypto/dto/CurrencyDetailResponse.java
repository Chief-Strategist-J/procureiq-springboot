package com.procureiq.springboot_app.features.crypto.dto;

import java.math.BigDecimal;

public record CurrencyDetailResponse(
        String symbol,
        String name,
        String type,
        BigDecimal currentPrice,
        BigDecimal change24hPercent,
        BigDecimal high24h,
        BigDecimal low24h,
        BigDecimal volume24h
) {}
