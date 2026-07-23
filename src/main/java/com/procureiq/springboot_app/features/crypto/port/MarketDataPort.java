package com.procureiq.springboot_app.features.crypto.port;

import com.procureiq.springboot_app.features.crypto.dto.*;

import java.util.List;

public interface MarketDataPort {

    KlineResponse       getKlines(String symbol, String interval, int limit);

    TickerResponse      getTicker(String symbol);

    OrderBookResponse   getOrderBook(String symbol, int limit);

    List<String>        getTradingSymbols();

    KlineResponse       getStockTimeSeries(String symbol);

    PriceResponse       getPrice(String symbol);

    BookTickerResponse  getBookTicker(String symbol);

    TradeResponse       getRecentTrades(String symbol, int limit);

    AvgPriceResponse    getAvgPrice(String symbol);

    List<TickerResponse> getAllTickers();

    MarketMoverResponse getMarketMovers(int topN);
}
