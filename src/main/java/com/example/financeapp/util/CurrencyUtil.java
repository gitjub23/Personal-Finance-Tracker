package com.example.financeapp.util;

import java.util.HashMap;
import java.util.Map;

public class CurrencyUtil {

    private static final Map<String, String> SYMBOLS = new HashMap<>();

    static {
        SYMBOLS.put("USD", "$");
        SYMBOLS.put("EUR", "€");
        SYMBOLS.put("HUF", "Ft");
        SYMBOLS.put("GBP", "£");
        SYMBOLS.put("JPY", "¥");
        // add more as needed
    }

    public static String getSymbol(String currencyCode) {
        if (currencyCode == null) return "$";
        return SYMBOLS.getOrDefault(currencyCode.toUpperCase(), currencyCode.toUpperCase());
    }
}