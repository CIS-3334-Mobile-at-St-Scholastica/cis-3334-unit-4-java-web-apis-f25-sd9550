package cis3334.java_webaip;


import java.util.HashMap;
import java.util.Map;

// Get currency conversion rates from
// https://ratesapi.io/documentation/
// https://api.ratesapi.io/api/latest?base=USD&symbols=EUR
// {"base":"USD","rates":{"EUR":0.8407600471},"date":"2021-03-09"}
public class CurrencyRate {

    private String base;
    private Map<String, Float> rates = new HashMap<String, Float>();
    private String date;

    public Float getEUR() {
        return rates.get("EUR");
    }

    @Override
    public String toString() {
        return "CurrencyRate = " + rates.get("EUR");
    }
}

