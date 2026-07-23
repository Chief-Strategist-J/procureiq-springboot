package com.procureiq.springboot_app.api.rest.v1.handlers;

import com.procureiq.springboot_app.features.crypto.dto.*;
import com.procureiq.springboot_app.features.crypto.service.CryptoService;
import com.procureiq.springboot_app.shared.types.single.ApiSingleResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.CRYPTO)
@CrossOrigin(origins = "*")
public class CryptoController {

    private final CryptoService cryptoService;

    public CryptoController(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    @GetMapping("/klines")
    public ResponseEntity<?> getBinanceKlines(
            @RequestParam(defaultValue = "BTCUSDT") String symbol,
            @RequestParam(defaultValue = "1h") String interval,
            @RequestParam(defaultValue = "100") int limit) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            KlineResponse response = cryptoService.getBinanceKlines(symbol, interval, limit);
            return ResponseEntity.ok(ApiSingleResponse.success(200, response));
        });
    }

    @GetMapping("/ticker")
    public ResponseEntity<?> get24hrTicker(@RequestParam(defaultValue = "BTCUSDT") String symbol) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            TickerResponse response = cryptoService.get24hrTicker(symbol);
            return ResponseEntity.ok(ApiSingleResponse.success(200, response));
        });
    }

    @GetMapping("/orderbook")
    public ResponseEntity<?> getOrderBook(
            @RequestParam(defaultValue = "BTCUSDT") String symbol,
            @RequestParam(defaultValue = "20") int limit) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            OrderBookResponse response = cryptoService.getOrderBook(symbol, limit);
            return ResponseEntity.ok(ApiSingleResponse.success(200, response));
        });
    }

    @GetMapping("/stocks")
    public ResponseEntity<?> getStockTimeSeries(@RequestParam(defaultValue = "IBM") String symbol) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            KlineResponse response = cryptoService.getStockTimeSeries(symbol);
            return ResponseEntity.ok(ApiSingleResponse.success(200, response));
        });
    }

    @GetMapping("/analyze")
    public ResponseEntity<?> analyzeMarketData(
            @RequestParam(defaultValue = "BTCUSDT") String symbol,
            @RequestParam(defaultValue = "1h") String interval,
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "12") int forecastHorizon) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            TimeSeriesAnalysisResponse response = cryptoService.analyzeMarketData(symbol, interval, limit, forecastHorizon);
            return ResponseEntity.ok(ApiSingleResponse.success(200, response));
        });
    }
}
