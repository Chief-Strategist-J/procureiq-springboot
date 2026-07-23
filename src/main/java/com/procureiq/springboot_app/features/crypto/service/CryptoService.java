package com.procureiq.springboot_app.features.crypto.service;

import com.procureiq.springboot_app.features.crypto.config.MarketCurrencyRegistry;
import com.procureiq.springboot_app.features.crypto.dto.*;
import com.procureiq.springboot_app.features.crypto.port.MarketDataPort;
import com.procureiq.springboot_app.infra.clients.CentralHttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CryptoService extends AbstractMarketDataService {

    @Value("${python.service.url}")
    private String pythonServiceUrl;

    public CryptoService(
            @Qualifier("binanceAdapter") MarketDataPort cryptoAdapter,
            @Qualifier("alphaVantageAdapter") MarketDataPort stockAdapter,
            MarketCurrencyRegistry currencyRegistry,
            CentralHttpClient httpClient) {
        super(cryptoAdapter, stockAdapter, currencyRegistry, httpClient);
    }

    public KlineResponse getBinanceKlines(String symbol, String interval, int limit)  { return fetchKlinesInternal(symbol, interval, limit); }

    public TickerResponse get24hrTicker(String symbol)                                 { return fetch24hrTickerInternal(symbol); }

    public List<TickerResponse>    getAllTickers()                                               { return fetchAllTickersInternal(); }

    public OrderBookResponse       getOrderBook(String symbol, int limit)                       { return fetchOrderBookInternal(symbol, limit); }

    public PriceResponse           getPrice(String symbol)                                      { return fetchPriceInternal(symbol); }

    public AvgPriceResponse        getAvgPrice(String symbol)                                   { return fetchAvgPriceInternal(symbol); }

    public BookTickerResponse      getBookTicker(String symbol)                                 { return fetchBookTickerInternal(symbol); }

    public TradeResponse           getRecentTrades(String symbol, int limit)                    { return fetchRecentTradesInternal(symbol, limit); }

    public MarketMoverResponse     getMarketMovers(int topN)                                    { return fetchMarketMoversInternal(topN); }

    public SymbolListResponse      getSupportedSymbols()                                        { return fetchSupportedSymbolsInternal(); }

    public CurrencyDetailResponse  getCurrencyDetails(String symbol)                            { return fetchCurrencyDetailsInternal(symbol); }

    public KlineResponse           getStockTimeSeries(String symbol)                            { return fetchStockTimeSeriesInternal(symbol); }

    public TimeSeriesAnalysisResponse analyzeMarketData(String symbol, String interval, int limit, int forecastHorizon) {
        return analyzeMarketDataInternal(pythonServiceUrl, symbol, interval, limit, forecastHorizon);
    }
}
