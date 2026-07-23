package com.procureiq.springboot_app.features.crypto.dto;

import java.math.BigDecimal;
import java.util.List;

public record TradeResponse(
        String          symbol,
        List<TradeItem> trades
) {
    public record TradeItem(
            Long        tradeId,
            BigDecimal  price,
            BigDecimal  qty,
            Long        time,
            boolean     isBuyerMaker
    ) {}
}
