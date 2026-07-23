package com.procureiq.springboot_app.features.crypto.config;

import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class MarketCurrencyRegistry {

    private static final List<String> DEFAULT_STOCKS = List.of("IBM", "AAPL", "MSFT", "GOOGL", "AMZN", "TSLA", "NVDA", "META");
    private static final List<String> DEFAULT_FIATS = List.of("USD", "EUR", "GBP", "JPY", "INR", "CAD", "AUD");

    public List<String> getStockSymbols() {
        return DEFAULT_STOCKS;
    }

    public List<String> getFiatCurrencies() {
        return DEFAULT_FIATS;
    }
}
