package com.procureiq.springboot_app.features.crypto.dto;

import java.util.List;

public record SymbolListResponse(
        List<String> cryptoSymbols,
        List<String> stockSymbols,
        List<String> fiatCurrencies
) {}
