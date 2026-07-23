package com.procureiq.springboot_app.features.crypto.adapter;

import com.procureiq.springboot_app.features.crypto.dto.*;
import com.procureiq.springboot_app.features.crypto.port.MarketDataPort;
import com.procureiq.springboot_app.infra.clients.CentralHttpClient;
import com.procureiq.springboot_app.infra.config.ApiEndpoints;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component("binanceAdapter")
public class BinanceMarketDataAdapter implements MarketDataPort {

    private final CentralHttpClient httpClient;

    public BinanceMarketDataAdapter(CentralHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public KlineResponse getKlines(String symbol, String interval, int limit) {
        List<?> raw = httpClient.get(ApiEndpoints.BINANCE_KLINES_URI, List.class,
                symbol.toUpperCase(), interval, limit);
        List<KlineResponse.KlineItem> items = Optional.ofNullable(raw).orElse(List.of())
                .stream()
                .filter(obj -> obj instanceof List<?>)
                .map(obj -> (List<?>) obj)
                .map(row -> new KlineResponse.KlineItem(
                        ((Number) row.get(0)).longValue(),
                        new BigDecimal((String) row.get(1)),
                        new BigDecimal((String) row.get(2)),
                        new BigDecimal((String) row.get(3)),
                        new BigDecimal((String) row.get(4)),
                        new BigDecimal((String) row.get(5)),
                        ((Number) row.get(6)).longValue()
                ))
                .collect(Collectors.toList());
        return new KlineResponse(symbol.toUpperCase(), interval, items);
    }

    @Override
    public TickerResponse getTicker(String symbol) {
        Map<?, ?> body = httpClient.get(ApiEndpoints.BINANCE_TICKER_24HR_URI, Map.class,
                symbol.toUpperCase());
        return Optional.ofNullable(body)
                .map(b -> buildTickerResponse(symbol.toUpperCase(), b))
                .orElse(emptyTicker(symbol));
    }

    @Override
    public OrderBookResponse getOrderBook(String symbol, int limit) {
        Map<?, ?> body = httpClient.get(ApiEndpoints.BINANCE_DEPTH_URI, Map.class,
                symbol.toUpperCase(), limit);
        return new OrderBookResponse(
                symbol.toUpperCase(),
                parsePriceLevels(body, ApiEndpoints.KEY_BIDS),
                parsePriceLevels(body, ApiEndpoints.KEY_ASKS)
        );
    }

    @Override
    public List<String> getTradingSymbols() {
        Map<?, ?> body = httpClient.get(ApiEndpoints.BINANCE_EXCHANGE_INFO_URI, Map.class);
        return Optional.ofNullable(body)
                .map(b -> (List<?>) b.get(ApiEndpoints.KEY_SYMBOLS))
                .orElse(List.of())
                .stream()
                .filter(item -> item instanceof Map<?, ?>)
                .map(item -> (Map<?, ?>) item)
                .filter(m -> ApiEndpoints.STATUS_TRADING.equals(m.get(ApiEndpoints.KEY_STATUS)))
                .map(m -> (String) m.get(ApiEndpoints.KEY_SYMBOL))
                .filter(sym -> sym != null && sym.endsWith(ApiEndpoints.SUFFIX_USDT))
                .limit(50)
                .collect(Collectors.toList());
    }

    @Override
    public KlineResponse getStockTimeSeries(String symbol) {
        return getKlines(ApiEndpoints.DEFAULT_SYMBOL_BTCUSDT,
                ApiEndpoints.DEFAULT_INTERVAL_DAILY,
                ApiEndpoints.DEFAULT_LIMIT_FALLBACK);
    }

    @Override
    public PriceResponse getPrice(String symbol) {
        Map<?, ?> body = httpClient.get(ApiEndpoints.BINANCE_PRICE_URI, Map.class,
                symbol.toUpperCase());
        BigDecimal price = Optional.ofNullable(body)
                .map(b -> new BigDecimal((String) b.get(ApiEndpoints.KEY_PRICE)))
                .orElse(BigDecimal.ZERO);
        return new PriceResponse(symbol.toUpperCase(), price);
    }

    @Override
    public BookTickerResponse getBookTicker(String symbol) {
        Map<?, ?> body = httpClient.get(ApiEndpoints.BINANCE_BOOK_TICKER_URI, Map.class,
                symbol.toUpperCase());
        return Optional.ofNullable(body)
                .map(b -> new BookTickerResponse(
                        symbol.toUpperCase(),
                        new BigDecimal((String) b.get(ApiEndpoints.KEY_BID_PRICE)),
                        new BigDecimal((String) b.get(ApiEndpoints.KEY_BID_QTY)),
                        new BigDecimal((String) b.get(ApiEndpoints.KEY_ASK_PRICE)),
                        new BigDecimal((String) b.get(ApiEndpoints.KEY_ASK_QTY))
                ))
                .orElse(new BookTickerResponse(symbol.toUpperCase(),
                        BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
    }

    @Override
    public TradeResponse getRecentTrades(String symbol, int limit) {
        List<?> raw = httpClient.get(ApiEndpoints.BINANCE_TRADES_URI, List.class,
                symbol.toUpperCase(), limit);
        List<TradeResponse.TradeItem> items = Optional.ofNullable(raw).orElse(List.of())
                .stream()
                .filter(obj -> obj instanceof Map<?, ?>)
                .map(obj -> (Map<?, ?>) obj)
                .map(m -> new TradeResponse.TradeItem(
                        ((Number) m.get(ApiEndpoints.KEY_TRADE_ID)).longValue(),
                        new BigDecimal((String) m.get(ApiEndpoints.KEY_PRICE)),
                        new BigDecimal((String) m.get(ApiEndpoints.KEY_QTY)),
                        ((Number) m.get(ApiEndpoints.KEY_TRADE_TIME)).longValue(),
                        Boolean.TRUE.equals(m.get(ApiEndpoints.KEY_IS_BUYER_MAKER))
                ))
                .collect(Collectors.toList());
        return new TradeResponse(symbol.toUpperCase(), items);
    }

    @Override
    public AvgPriceResponse getAvgPrice(String symbol) {
        Map<?, ?> body = httpClient.get(ApiEndpoints.BINANCE_AVG_PRICE_URI, Map.class,
                symbol.toUpperCase());
        return Optional.ofNullable(body)
                .map(b -> new AvgPriceResponse(
                        symbol.toUpperCase(),
                        ((Number) b.get(ApiEndpoints.KEY_MINS)).intValue(),
                        new BigDecimal((String) b.get(ApiEndpoints.KEY_PRICE))
                ))
                .orElse(new AvgPriceResponse(symbol.toUpperCase(), 0, BigDecimal.ZERO));
    }

    @Override
    public List<TickerResponse> getAllTickers() {
        List<?> raw = httpClient.get(ApiEndpoints.BINANCE_TICKER_ALL_URI, List.class);
        return Optional.ofNullable(raw).orElse(List.of())
                .stream()
                .filter(obj -> obj instanceof Map<?, ?>)
                .map(obj -> (Map<?, ?>) obj)
                .filter(m -> {
                    String sym = (String) m.get(ApiEndpoints.KEY_SYMBOL);
                    return sym != null && sym.endsWith(ApiEndpoints.SUFFIX_USDT);
                })
                .map(m -> buildTickerResponse((String) m.get(ApiEndpoints.KEY_SYMBOL), m))
                .collect(Collectors.toList());
    }

    @Override
    public MarketMoverResponse getMarketMovers(int topN) {
        List<TickerResponse> all = getAllTickers();
        Comparator<TickerResponse> byChange =
                Comparator.comparing(TickerResponse::priceChangePercent);
        List<MarketMoverResponse.Mover> gainers = all.stream()
                .sorted(byChange.reversed())
                .limit(topN)
                .map(t -> new MarketMoverResponse.Mover(
                        t.symbol(), t.priceChangePercent(), t.price(), t.volume()))
                .collect(Collectors.toList());
        List<MarketMoverResponse.Mover> losers = all.stream()
                .sorted(byChange)
                .limit(topN)
                .map(t -> new MarketMoverResponse.Mover(
                        t.symbol(), t.priceChangePercent(), t.price(), t.volume()))
                .collect(Collectors.toList());
        return new MarketMoverResponse(gainers, losers);
    }


    private TickerResponse buildTickerResponse(String symbol, Map<?, ?> b) {
        return new TickerResponse(
                symbol,
                new BigDecimal((String) b.get(ApiEndpoints.KEY_LAST_PRICE)),
                new BigDecimal((String) b.get(ApiEndpoints.KEY_PRICE_CHANGE_PERCENT)),
                new BigDecimal((String) b.get(ApiEndpoints.KEY_HIGH_PRICE)),
                new BigDecimal((String) b.get(ApiEndpoints.KEY_LOW_PRICE)),
                new BigDecimal((String) b.get(ApiEndpoints.KEY_VOLUME))
        );
    }

    private TickerResponse emptyTicker(String symbol) {
        return new TickerResponse(symbol.toUpperCase(),
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    private List<OrderBookResponse.PriceLevel> parsePriceLevels(Map<?, ?> body, String side) {
        return Optional.ofNullable(body)
                .map(b -> (List<?>) b.get(side))
                .orElse(List.of())
                .stream()
                .filter(item -> item instanceof List<?>)
                .map(item -> (List<?>) item)
                .map(row -> new OrderBookResponse.PriceLevel(
                        new BigDecimal((String) row.get(0)),
                        new BigDecimal((String) row.get(1))
                ))
                .collect(Collectors.toList());
    }
}
