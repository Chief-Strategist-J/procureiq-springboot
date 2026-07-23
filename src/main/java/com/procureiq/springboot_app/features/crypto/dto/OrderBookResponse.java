package com.procureiq.springboot_app.features.crypto.dto;

import java.math.BigDecimal;
import java.util.List;

public record OrderBookResponse(
        String symbol,
        List<PriceLevel> bids,
        List<PriceLevel> asks
) {
    public record PriceLevel(
            BigDecimal price,
            BigDecimal quantity
    ) {}
}
