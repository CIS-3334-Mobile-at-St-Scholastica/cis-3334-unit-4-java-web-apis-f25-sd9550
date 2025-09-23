package cis3334.java_webaip;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.util.Map;

/**
 * ViewModelCurrency using Volley + Moshi.
 *
 * Example API: https://api.exchangerate.host/latest?base=USD&symbols=EUR
 */
public class ViewModelCurrency extends AndroidViewModel {

    private static final String BASE_URL = "https://api.exchangerate.host/latest";

    private final RequestQueue queue;
    private final Moshi moshi = new Moshi.Builder().build();
    private final JsonAdapter<CurrencyRateResponse> adapter =
            moshi.adapter(CurrencyRateResponse.class);

    private final MutableLiveData<CurrencyRate> rate = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public ViewModelCurrency(Application app) {
        super(app);
        queue = Volley.newRequestQueue(app.getApplicationContext());
    }

    public LiveData<CurrencyRate> getRate() { return rate; }
    public LiveData<String> getError() { return error; }

    public void fetchRate(String base, String target) {
        final String safeBase = (base == null || base.trim().isEmpty()) ? "USD" : base.trim().toUpperCase();
        final String safeTarget = (target == null || target.trim().isEmpty()) ? "EUR" : target.trim().toUpperCase();

        final String url = BASE_URL + "?base=" + safeBase + "&symbols=" + safeTarget;

        StringRequest req = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    try {
                        CurrencyRateResponse parsed = adapter.fromJson(response);
                        if (parsed == null || parsed.rates == null || !parsed.rates.containsKey(safeTarget)) {
                            error.setValue("Invalid response");
                        } else {
                            Double value = parsed.rates.get(safeTarget);
                            CurrencyRate result = new CurrencyRate(safeBase, safeTarget, value, parsed.date);
                            rate.setValue(result);
                        }
                    } catch (Exception ex) {
                        error.setValue("Parse error: " + ex.getMessage());
                    }
                },
                volleyError -> error.setValue("Volley error: " + volleyError.getMessage())
        );

        queue.add(req);
    }

    // ---------------------------
    // -- Response/Data Models ---
    // ---------------------------

    /** Matches exchangerate.host JSON. */
    public static class CurrencyRateResponse {
        public String base;
        public Map<String, Double> rates;
        public String date;
    }

    /** DTO for the UI */
    public static class CurrencyRate {
        public final String base;
        public final String target;
        public final Double rate;
        public final String date;

        public CurrencyRate(String base, String target, Double rate, String date) {
            this.base = base;
            this.target = target;
            this.rate = rate;
            this.date = date;
        }

        @Override
        public String toString() {
            return base + " → " + target + " = " + rate + " (as of " + date + ")";
        }
    }
}
