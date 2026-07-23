package com.procureiq.springboot_app.features.crypto.dto;

import java.math.BigDecimal;
import java.util.List;

public record TimeSeriesAnalysisRequest(
        String symbol,
        String interval,
        int forecastHorizon,
        List<KlineResponse.KlineItem> klines
) {}
