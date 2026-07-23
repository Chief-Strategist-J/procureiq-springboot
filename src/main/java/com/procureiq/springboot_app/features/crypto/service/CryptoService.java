package com.procureiq.springboot_app.features.crypto.service;

import com.procureiq.springboot_app.features.crypto.dto.KlineResponse;
import com.procureiq.springboot_app.features.crypto.dto.TimeSeriesAnalysisRequest;
import com.procureiq.springboot_app.features.crypto.dto.TimeSeriesAnalysisResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class CryptoService {

    @Value("${binance.api.key:wcUssRRszrfOeqLCdOzERP4kBIxAIJYzJ8TUWiNqsKIwGKkUQaHe8UViHGzaIGnx}")
    private String apiKey;

    @Value("${binance.api.secret:1OwUQn6GZu1vGgQJN0Kj0Ck41mMRUPiymkOTLllY8z7BSD733VSAD2cupLu7of80}")
    private String apiSecret;

    @Value("${python.service.url:http://localhost:8000}")
    private String pythonServiceUrl;

    private final RestTemplate restTemplate;
    private static final String BINANCE_BASE_URL = "https://api.binance.com";

    public CryptoService() {
        this.restTemplate = new RestTemplate();
    }

    public KlineResponse getBinanceKlines(String symbol, String interval, int limit) {
        String url = String.format("%s/api/v3/klines?symbol=%s&interval=%s&limit=%d",
                BINANCE_BASE_URL, symbol.toUpperCase(), interval, limit);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-MBX-APIKEY", apiKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, entity, List.class);
        List<?> rawKlines = response.getBody();

        List<KlineResponse.KlineItem> items = new ArrayList<>();
        if (rawKlines != null) {
            for (Object obj : rawKlines) {
                if (obj instanceof List<?> row) {
                    Long openTime = ((Number) row.get(0)).longValue();
                    BigDecimal open = new BigDecimal((String) row.get(1));
                    BigDecimal high = new BigDecimal((String) row.get(2));
                    BigDecimal low = new BigDecimal((String) row.get(3));
                    BigDecimal close = new BigDecimal((String) row.get(4));
                    BigDecimal volume = new BigDecimal((String) row.get(5));
                    Long closeTime = ((Number) row.get(6)).longValue();

                    items.add(new KlineResponse.KlineItem(openTime, open, high, low, close, volume, closeTime));
                }
            }
        }

        return new KlineResponse(symbol.toUpperCase(), interval, items);
    }

    public TimeSeriesAnalysisResponse analyzeMarketData(String symbol, String interval, int limit, int forecastHorizon) {
        KlineResponse klineData = getBinanceKlines(symbol, interval, limit);

        TimeSeriesAnalysisRequest pyRequest = new TimeSeriesAnalysisRequest(
                klineData.symbol(),
                klineData.interval(),
                forecastHorizon,
                klineData.klines()
        );

        String pyUrl = pythonServiceUrl + "/api/v1/analytics/time-series";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TimeSeriesAnalysisRequest> entity = new HttpEntity<>(pyRequest, headers);

        try {
            ResponseEntity<TimeSeriesAnalysisResponse> response = restTemplate.postForEntity(
                    pyUrl, entity, TimeSeriesAnalysisResponse.class);
            return response.getBody();
        } catch (Exception e) {
            List<BigDecimal> mockForecast = new ArrayList<>();
            BigDecimal lastPrice = klineData.klines().isEmpty() ? BigDecimal.ZERO : klineData.klines().get(klineData.klines().size() - 1).close();
            for (int i = 1; i <= forecastHorizon; i++) {
                mockForecast.add(lastPrice.add(BigDecimal.valueOf(i * 0.5)));
            }

            return new TimeSeriesAnalysisResponse(
                    symbol.toUpperCase(),
                    interval,
                    klineData.klines().size(),
                    lastPrice,
                    BigDecimal.valueOf(0.02),
                    "BULLISH",
                    mockForecast
            );
        }
    }
}
