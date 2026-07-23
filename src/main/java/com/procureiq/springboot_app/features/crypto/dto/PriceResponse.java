package com.procureiq.springboot_app.features.crypto.dto;

import java.math.BigDecimal;

public record PriceResponse(
        String      symbol,
        BigDecimal  price
) {}
