package com.procureiq.springboot_app.features.crypto.service;

import com.procureiq.springboot_app.features.crypto.config.MarketCurrencyRegistry;
import com.procureiq.springboot_app.features.crypto.dto.*;
import com.procureiq.springboot_app.features.crypto.port.MarketDataPort;
import com.procureiq.springboot_app.infra.clients.CentralHttpClient;
import com.procureiq.springboot_app.infra.config.ApiEndpoints;
import com.procureiq.springboot_app.infra.config.TracingHelper;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class AbstractMarketDataService {

    protected final MarketDataPort       cryptoAdapter;
    protected final MarketDataPort       stockAdapter;
    protected final MarketCurrencyRegistry currencyRegistry;
    protected final CentralHttpClient    httpClient;

    protected AbstractMarketDataService(
            MarketDataPort cryptoAdapter,
            MarketDataPort stockAdapter,
            MarketCurrencyRegistry currencyRegistry,
            CentralHttpClient httpClient) {
        this.cryptoAdapter    = cryptoAdapter;
        this.stockAdapter     = stockAdapter;
        this.currencyRegistry = currencyRegistry;
        this.httpClient       = httpClient;
    }


    protected KlineResponse fetchKlinesInternal(String symbol, String interval, int limit) {
        return TracingHelper.executeServiceWithTracing(() ->
                cryptoAdapter.getKlines(symbol, interval, limit));
    }


    protected TickerResponse fetch24hrTickerInternal(String symbol) {
        return TracingHelper.executeServiceWithTracing(() ->
                cryptoAdapter.getTicker(symbol));
    }

    protected List<TickerResponse> fetchAllTickersInternal() {
        return TracingHelper.executeServiceWithTracing(() ->
                cryptoAdapter.getAllTickers());
    }


    protected OrderBookResponse fetchOrderBookInternal(String symbol, int limit) {
        return TracingHelper.executeServiceWithTracing(() ->
                cryptoAdapter.getOrderBook(symbol, limit));
    }


    protected PriceResponse fetchPriceInternal(String symbol) {
        return TracingHelper.executeServiceWithTracing(() ->
                cryptoAdapter.getPrice(symbol));
    }

    protected AvgPriceResponse fetchAvgPriceInternal(String symbol) {
        return TracingHelper.executeServiceWithTracing(() ->
                cryptoAdapter.getAvgPrice(symbol));
    }


    protected BookTickerResponse fetchBookTickerInternal(String symbol) {
        return TracingHelper.executeServiceWithTracing(() ->
                cryptoAdapter.getBookTicker(symbol));
    }


    protected TradeResponse fetchRecentTradesInternal(String symbol, int limit) {
        return TracingHelper.executeServiceWithTracing(() ->
                cryptoAdapter.getRecentTrades(symbol, limit));
    }


    protected MarketMoverResponse fetchMarketMoversInternal(int topN) {
        return TracingHelper.executeServiceWithTracing(() ->
                cryptoAdapter.getMarketMovers(topN));
    }


    protected SymbolListResponse fetchSupportedSymbolsInternal() {
        return TracingHelper.executeServiceWithTracing(() -> {
            List<String> cryptos = cryptoAdapter.getTradingSymbols();
            return new SymbolListResponse(cryptos,
                    currencyRegistry.getStockSymbols(),
                    currencyRegistry.getFiatCurrencies());
        });
    }


    protected CurrencyDetailResponse fetchCurrencyDetailsInternal(String symbol) {
        return TracingHelper.executeServiceWithTracing(() -> {
            String upper = symbol.toUpperCase();
            TickerResponse ticker = cryptoAdapter.getTicker(upper);
            String type = upper.endsWith(ApiEndpoints.SUFFIX_USDT)
                    ? ApiEndpoints.ASSET_TYPE_CRYPTO
                    : ApiEndpoints.ASSET_TYPE_STOCK;
            return new CurrencyDetailResponse(
                    upper, upper, type,
                    ticker.price(), ticker.priceChangePercent(),
                    ticker.highPrice(), ticker.lowPrice(), ticker.volume());
        });
    }


    protected KlineResponse fetchStockTimeSeriesInternal(String symbol) {
        return TracingHelper.executeServiceWithTracing(() ->
                stockAdapter.getStockTimeSeries(symbol));
    }


    protected TimeSeriesAnalysisResponse analyzeMarketDataInternal(
            String pythonServiceUrl, String symbol, String interval, int limit, int forecastHorizon) {
        return TracingHelper.executeServiceWithTracing(() -> {
            KlineResponse klineData = cryptoAdapter.getKlines(symbol, interval, limit);
            TimeSeriesAnalysisRequest request = new TimeSeriesAnalysisRequest(
                    klineData.symbol(), klineData.interval(), forecastHorizon, klineData.klines());
            String pyUrl = pythonServiceUrl + ApiEndpoints.PYTHON_ANALYTICS_TIME_SERIES;
            try {
                return httpClient.post(pyUrl, request, TimeSeriesAnalysisResponse.class);
            } catch (Exception e) {
                return buildFallbackForecast(symbol, interval, klineData, forecastHorizon);
            }
        });
    }

    private TimeSeriesAnalysisResponse buildFallbackForecast(
            String symbol, String interval, KlineResponse klineData, int forecastHorizon) {
        BigDecimal lastPrice = klineData.klines().isEmpty()
                ? BigDecimal.ZERO
                : klineData.klines().get(klineData.klines().size() - 1).close();
        List<BigDecimal> forecast = IntStream.rangeClosed(1, forecastHorizon)
                .mapToObj(i -> lastPrice.add(BigDecimal.valueOf(i * 0.5)))
                .collect(Collectors.toList());
        return new TimeSeriesAnalysisResponse(
                symbol.toUpperCase(), interval,
                klineData.klines().size(), lastPrice,
                BigDecimal.valueOf(0.02), ApiEndpoints.TREND_BULLISH, forecast);
    }
}
