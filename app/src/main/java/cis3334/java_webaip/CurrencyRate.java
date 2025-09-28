package cis3334.java_webaip;
import android.util.Log;

import java.io.*;
import okhttp3.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

// Get currency conversion rates from
// https://ratesapi.io/documentation/
// https://api.ratesapi.io/api/latest?base=USD&symbols=EUR
// {"base":"USD","rates":{"EUR":0.8407600471},"date":"2021-03-09"}
public class CurrencyRate {

    private boolean success;
    private String date;
    private Map<String, Double> rates;

    public boolean isSuccess() {
        return success;
    }

    public String getDate() {
        return date;
    }

    public Double getRate(String currency) {
        return rates.get(currency);
    }

    public String getDescription() {
        //return rates.entrySet().stream().map(e -> e.getKey() + " -> " + e.getValue()).collect(Collectors.joining("\n"));
        String currentCurrencies = "USD TO EUR RATE:\n";
        return currentCurrencies + Objects.requireNonNull(rates.get("EUR"));
    }

}

