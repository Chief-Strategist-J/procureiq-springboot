package com.procureiq.springboot_app.features.crypto.dto;

import java.math.BigDecimal;
import java.util.List;

public record TimeSeriesAnalysisResponse(
        String symbol,
        String interval,
        int dataPointsCount,
        BigDecimal meanPrice,
        BigDecimal priceVolatility,
        String trendDirection,
        List<BigDecimal> forecastPrices
) {}
