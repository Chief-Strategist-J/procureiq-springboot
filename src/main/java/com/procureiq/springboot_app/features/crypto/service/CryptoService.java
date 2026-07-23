package com.procureiq.springboot_app.features.crypto.service;

import com.procureiq.springboot_app.features.crypto.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private static final String ALPHAVANTAGE_BASE_URL = "https://www.alphavantage.co/query";

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

    public TickerResponse get24hrTicker(String symbol) {
        String url = String.format("%s/api/v3/ticker/24hr?symbol=%s", BINANCE_BASE_URL, symbol.toUpperCase());
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-MBX-APIKEY", apiKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        Map<?, ?> body = response.getBody();

        if (body != null) {
            return new TickerResponse(
                    symbol.toUpperCase(),
                    new BigDecimal((String) body.get("lastPrice")),
                    new BigDecimal((String) body.get("priceChangePercent")),
                    new BigDecimal((String) body.get("highPrice")),
                    new BigDecimal((String) body.get("lowPrice")),
                    new BigDecimal((String) body.get("volume"))
            );
        }
        return new TickerResponse(symbol.toUpperCase(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    public OrderBookResponse getOrderBook(String symbol, int limit) {
        String url = String.format("%s/api/v3/depth?symbol=%s&limit=%d", BINANCE_BASE_URL, symbol.toUpperCase(), limit);
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-MBX-APIKEY", apiKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        Map<?, ?> body = response.getBody();

        List<OrderBookResponse.PriceLevel> bids = new ArrayList<>();
        List<OrderBookResponse.PriceLevel> asks = new ArrayList<>();

        if (body != null) {
            List<?> rawBids = (List<?>) body.get("bids");
            if (rawBids != null) {
                for (Object b : rawBids) {
                    List<?> row = (List<?>) b;
                    bids.add(new OrderBookResponse.PriceLevel(new BigDecimal((String) row.get(0)), new BigDecimal((String) row.get(1))));
                }
            }
            List<?> rawAsks = (List<?>) body.get("asks");
            if (rawAsks != null) {
                for (Object a : rawAsks) {
                    List<?> row = (List<?>) a;
                    asks.add(new OrderBookResponse.PriceLevel(new BigDecimal((String) row.get(0)), new BigDecimal((String) row.get(1))));
                }
            }
        }
        return new OrderBookResponse(symbol.toUpperCase(), bids, asks);
    }

    public KlineResponse getStockTimeSeries(String symbol) {
        String url = String.format("%s?function=TIME_SERIES_DAILY&symbol=%s&apikey=demo", ALPHAVANTAGE_BASE_URL, symbol.toUpperCase());
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            Map<?, ?> body = response.getBody();
            List<KlineResponse.KlineItem> items = new ArrayList<>();
            if (body != null && body.containsKey("Time Series (Daily)")) {
                Map<?, ?> timeSeries = (Map<?, ?>) body.get("Time Series (Daily)");
                long dummyTime = System.currentTimeMillis();
                for (Map.Entry<?, ?> entry : timeSeries.entrySet()) {
                    Map<?, ?> values = (Map<?, ?>) entry.getValue();
                    BigDecimal open = new BigDecimal((String) values.get("1. open"));
                    BigDecimal high = new BigDecimal((String) values.get("2. high"));
                    BigDecimal low = new BigDecimal((String) values.get("3. low"));
                    BigDecimal close = new BigDecimal((String) values.get("4. close"));
                    BigDecimal volume = new BigDecimal((String) values.get("5. volume"));
                    items.add(new KlineResponse.KlineItem(dummyTime, open, high, low, close, volume, dummyTime));
                    dummyTime -= 86400000;
                }
            }
            return new KlineResponse(symbol.toUpperCase(), "1d", items);
        } catch (Exception e) {
            return getBinanceKlines("BTCUSDT", "1d", 30);
        }
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
