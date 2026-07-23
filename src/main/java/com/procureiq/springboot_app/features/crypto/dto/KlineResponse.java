package com.procureiq.springboot_app.features.crypto.dto;

import java.math.BigDecimal;
import java.util.List;

public record KlineResponse(
        String symbol,
        String interval,
        List<KlineItem> klines
) {
    public record KlineItem(
            Long openTime,
            BigDecimal open,
            BigDecimal high,
            BigDecimal low,
            BigDecimal close,
            BigDecimal volume,
            Long closeTime
    ) {}
}
