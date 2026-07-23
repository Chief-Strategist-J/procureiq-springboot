package com.procureiq.springboot_app.features.crypto.adapter;

import com.procureiq.springboot_app.features.crypto.dto.*;
import com.procureiq.springboot_app.features.crypto.port.MarketDataPort;
import com.procureiq.springboot_app.infra.clients.CentralHttpClient;
import com.procureiq.springboot_app.infra.config.ApiEndpoints;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component("alphaVantageAdapter")
public class AlphaVantageMarketDataAdapter implements MarketDataPort {

    private final CentralHttpClient httpClient;

    public AlphaVantageMarketDataAdapter(CentralHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public KlineResponse getKlines(String symbol, String interval, int limit) {
        return getStockTimeSeries(symbol);
    }

    @Override
    public TickerResponse getTicker(String symbol) {
        return emptyTicker(symbol);
    }

    @Override
    public OrderBookResponse getOrderBook(String symbol, int limit) {
        return new OrderBookResponse(symbol.toUpperCase(), List.of(), List.of());
    }

    @Override
    public List<String> getTradingSymbols() {
        return List.of();
    }

    @Override
    public KlineResponse getStockTimeSeries(String symbol) {
        Map<?, ?> body = httpClient.get(ApiEndpoints.ALPHAVANTAGE_DAILY_TIME_SERIES_URI, Map.class,
                symbol.toUpperCase(), ApiEndpoints.APIKEY_DEMO);
        Map<?, ?> timeSeries = findTimeSeriesMap(body);
        return new KlineResponse(symbol.toUpperCase(), ApiEndpoints.DEFAULT_INTERVAL_DAILY,
                parseTimeSeriesItems(timeSeries));
    }

    @Override
    public PriceResponse getPrice(String symbol) {
        return new PriceResponse(symbol.toUpperCase(), BigDecimal.ZERO);
    }

    @Override
    public BookTickerResponse getBookTicker(String symbol) {
        return new BookTickerResponse(symbol.toUpperCase(),
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    @Override
    public TradeResponse getRecentTrades(String symbol, int limit) {
        return new TradeResponse(symbol.toUpperCase(), List.of());
    }

    @Override
    public AvgPriceResponse getAvgPrice(String symbol) {
        return new AvgPriceResponse(symbol.toUpperCase(), 0, BigDecimal.ZERO);
    }

    @Override
    public List<TickerResponse> getAllTickers() {
        return List.of();
    }

    @Override
    public MarketMoverResponse getMarketMovers(int topN) {
        return new MarketMoverResponse(List.of(), List.of());
    }


    private Map<?, ?> findTimeSeriesMap(Map<?, ?> body) {
        return Optional.ofNullable(body)
                .flatMap(b -> b.entrySet().stream()
                        .filter(e -> {
                            String k = String.valueOf(e.getKey()).toLowerCase();
                            return k.contains(ApiEndpoints.KEY_TIME_SERIES)
                                    || k.contains(ApiEndpoints.KEY_SERIES)
                                    || k.contains(ApiEndpoints.KEY_DAILY);
                        })
                        .filter(e -> e.getValue() instanceof Map<?, ?>)
                        .map(e -> (Map<?, ?>) e.getValue())
                        .findFirst()
                )
                .orElse(null);
    }

    private List<KlineResponse.KlineItem> parseTimeSeriesItems(Map<?, ?> timeSeries) {
        if (timeSeries == null) return List.of();
        final long[] time = {System.currentTimeMillis()};
        return timeSeries.entrySet().stream()
                .filter(e -> e.getValue() instanceof Map<?, ?>)
                .map(e -> (Map<?, ?>) e.getValue())
                .map(values -> {
                    BigDecimal[] ohlcv = extractOhlcv(values);
                    long t = time[0];
                    time[0] -= 86400000L;
                    return new KlineResponse.KlineItem(t, ohlcv[0], ohlcv[1], ohlcv[2], ohlcv[3], ohlcv[4], t);
                })
                .collect(Collectors.toList());
    }

    private BigDecimal[] extractOhlcv(Map<?, ?> values) {
        BigDecimal[] result = {BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO};
        values.entrySet().forEach(e -> {
            String k = String.valueOf(e.getKey()).toLowerCase();
            BigDecimal v = new BigDecimal(String.valueOf(e.getValue()));
            if      (k.contains(ApiEndpoints.KEY_OPEN))   result[0] = v;
            else if (k.contains(ApiEndpoints.KEY_HIGH))   result[1] = v;
            else if (k.contains(ApiEndpoints.KEY_LOW))    result[2] = v;
            else if (k.contains(ApiEndpoints.KEY_CLOSE))  result[3] = v;
            else if (k.contains(ApiEndpoints.KEY_VOLUME)) result[4] = v;
        });
        return result;
    }

    private TickerResponse emptyTicker(String symbol) {
        return new TickerResponse(symbol.toUpperCase(),
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }
}
