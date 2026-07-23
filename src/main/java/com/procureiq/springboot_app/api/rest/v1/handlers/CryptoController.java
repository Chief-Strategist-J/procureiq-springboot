package com.procureiq.springboot_app.api.rest.v1.handlers;

import com.procureiq.springboot_app.features.crypto.dto.*;
import com.procureiq.springboot_app.features.crypto.service.CryptoService;
import com.procureiq.springboot_app.infra.config.ApiEndpoints;
import com.procureiq.springboot_app.infra.config.TracingHelper;
import com.procureiq.springboot_app.shared.types.single.ApiSingleResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiEndpoints.CRYPTO)
@CrossOrigin(origins = "*")
public class CryptoController {

    private final CryptoService cryptoService;

    public CryptoController(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }


    @GetMapping(ApiEndpoints.KLINES)
    public ResponseEntity<?> getBinanceKlines(
            @RequestParam(defaultValue = ApiEndpoints.DEFAULT_SYMBOL_BTCUSDT) String symbol,
            @RequestParam(defaultValue = ApiEndpoints.DEFAULT_INTERVAL_DAILY)  String interval,
            @RequestParam(defaultValue = "100") int limit) {
        return TracingHelper.executeWithTracing(() -> {
            KlineResponse data = cryptoService.getBinanceKlines(symbol, interval, limit);
            return ResponseEntity.ok(ApiSingleResponse.success(200, data));
        });
    }


    @GetMapping(ApiEndpoints.TICKER)
    public ResponseEntity<?> get24hrTicker(
            @RequestParam(defaultValue = ApiEndpoints.DEFAULT_SYMBOL_BTCUSDT) String symbol) {
        return TracingHelper.executeWithTracing(() -> {
            TickerResponse data = cryptoService.get24hrTicker(symbol);
            return ResponseEntity.ok(ApiSingleResponse.success(200, data));
        });
    }

    @GetMapping(ApiEndpoints.ALL_TICKERS)
    public ResponseEntity<?> getAllTickers() {
        return TracingHelper.executeWithTracing(() -> {
            List<TickerResponse> data = cryptoService.getAllTickers();
            return ResponseEntity.ok(ApiSingleResponse.success(200, data));
        });
    }


    @GetMapping(ApiEndpoints.PRICE)
    public ResponseEntity<?> getPrice(
            @RequestParam(defaultValue = ApiEndpoints.DEFAULT_SYMBOL_BTCUSDT) String symbol) {
        return TracingHelper.executeWithTracing(() -> {
            PriceResponse data = cryptoService.getPrice(symbol);
            return ResponseEntity.ok(ApiSingleResponse.success(200, data));
        });
    }

    @GetMapping(ApiEndpoints.AVG_PRICE)
    public ResponseEntity<?> getAvgPrice(
            @RequestParam(defaultValue = ApiEndpoints.DEFAULT_SYMBOL_BTCUSDT) String symbol) {
        return TracingHelper.executeWithTracing(() -> {
            AvgPriceResponse data = cryptoService.getAvgPrice(symbol);
            return ResponseEntity.ok(ApiSingleResponse.success(200, data));
        });
    }


    @GetMapping(ApiEndpoints.BOOK_TICKER)
    public ResponseEntity<?> getBookTicker(
            @RequestParam(defaultValue = ApiEndpoints.DEFAULT_SYMBOL_BTCUSDT) String symbol) {
        return TracingHelper.executeWithTracing(() -> {
            BookTickerResponse data = cryptoService.getBookTicker(symbol);
            return ResponseEntity.ok(ApiSingleResponse.success(200, data));
        });
    }


    @GetMapping(ApiEndpoints.ORDERBOOK)
    public ResponseEntity<?> getOrderBook(
            @RequestParam(defaultValue = ApiEndpoints.DEFAULT_SYMBOL_BTCUSDT) String symbol,
            @RequestParam(defaultValue = "20") int limit) {
        return TracingHelper.executeWithTracing(() -> {
            OrderBookResponse data = cryptoService.getOrderBook(symbol, limit);
            return ResponseEntity.ok(ApiSingleResponse.success(200, data));
        });
    }


    @GetMapping(ApiEndpoints.TRADES)
    public ResponseEntity<?> getRecentTrades(
            @RequestParam(defaultValue = ApiEndpoints.DEFAULT_SYMBOL_BTCUSDT) String symbol,
            @RequestParam(defaultValue = "50") int limit) {
        return TracingHelper.executeWithTracing(() -> {
            TradeResponse data = cryptoService.getRecentTrades(symbol, limit);
            return ResponseEntity.ok(ApiSingleResponse.success(200, data));
        });
    }


    @GetMapping(ApiEndpoints.MOVERS)
    public ResponseEntity<?> getMarketMovers(
            @RequestParam(defaultValue = "10") int topN) {
        return TracingHelper.executeWithTracing(() -> {
            MarketMoverResponse data = cryptoService.getMarketMovers(topN);
            return ResponseEntity.ok(ApiSingleResponse.success(200, data));
        });
    }


    @GetMapping(ApiEndpoints.SYMBOLS)
    public ResponseEntity<?> getSupportedSymbols() {
        return TracingHelper.executeWithTracing(() -> {
            SymbolListResponse data = cryptoService.getSupportedSymbols();
            return ResponseEntity.ok(ApiSingleResponse.success(200, data));
        });
    }

    @GetMapping(ApiEndpoints.DETAILS)
    public ResponseEntity<?> getCurrencyDetails(
            @RequestParam(defaultValue = ApiEndpoints.DEFAULT_SYMBOL_BTCUSDT) String symbol) {
        return TracingHelper.executeWithTracing(() -> {
            CurrencyDetailResponse data = cryptoService.getCurrencyDetails(symbol);
            return ResponseEntity.ok(ApiSingleResponse.success(200, data));
        });
    }


    @GetMapping(ApiEndpoints.STOCKS)
    public ResponseEntity<?> getStockTimeSeries(
            @RequestParam String symbol) {
        return TracingHelper.executeWithTracing(() -> {
            KlineResponse data = cryptoService.getStockTimeSeries(symbol);
            return ResponseEntity.ok(ApiSingleResponse.success(200, data));
        });
    }


    @GetMapping(ApiEndpoints.ANALYZE)
    public ResponseEntity<?> analyzeMarketData(
            @RequestParam(defaultValue = ApiEndpoints.DEFAULT_SYMBOL_BTCUSDT) String symbol,
            @RequestParam(defaultValue = ApiEndpoints.DEFAULT_INTERVAL_DAILY)  String interval,
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "12")  int forecastHorizon) {
        return TracingHelper.executeWithTracing(() -> {
            TimeSeriesAnalysisResponse data = cryptoService.analyzeMarketData(symbol, interval, limit, forecastHorizon);
            return ResponseEntity.ok(ApiSingleResponse.success(200, data));
        });
    }
}
