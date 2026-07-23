package com.procureiq.springboot_app.features.crypto.dto;

import java.math.BigDecimal;
import java.util.List;

public record MarketMoverResponse(
        List<Mover> topGainers,
        List<Mover> topLosers
) {
    public record Mover(
            String      symbol,
            BigDecimal  priceChangePercent,
            BigDecimal  lastPrice,
            BigDecimal  volume
    ) {}
}
