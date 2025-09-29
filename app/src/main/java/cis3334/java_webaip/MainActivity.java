package cis3334.java_webaip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    TextView textViewStatus;
    Button buttonGetFact, buttonCurrency, buttonWeather, buttonSpaceNews, buttonStudentAPI;
    ViewModelCurrency viewModelCurrency;

    // One shared Volley queue for the whole Activity (better than creating one per request)
    private RequestQueue mRequestQueue;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewStatus = findViewById(R.id.textViewStatus);

        // Shared Volley queue
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());

        // If your ViewModelCurrency extends AndroidViewModel, use the AndroidViewModelFactory:
        viewModelCurrency = new ViewModelProvider(
                this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())
        ).get(ViewModelCurrency.class);

        // Observe LiveData so UI updates when async result arrives
        viewModelCurrency.getRate().observe(this, rate -> {
            if (rate != null) {
                // Example: "Euro rate = 0.93 (as of 2025-09-22)"
                textViewStatus.setText("Euro rate = " + rate.rate + " (as of " + rate.date + ")");
            }
        });

        viewModelCurrency.getError().observe(this, msg -> {
            if (msg != null) {
                textViewStatus.setText("Error: " + msg);
            }
        });

        setupButtonDogFact();
        setupButtonCurrency();
        setupButtonWeather();
        setupButtonSpaceNews();
        setupButtonStudentAPI();
    }

    private void getStudentAPI() {
        // ======================= Student must add code here to get JSON data from an API =======================
        String url = "https://today.zenquotes.io/api";

        @SuppressLint("SetTextI18n") JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    Moshi moshi = new Moshi.Builder().build();
                    JsonAdapter<StudentDay> adapter = moshi.adapter(StudentDay.class);
                    try {
                        StudentDay studentDay = adapter.fromJson(response.toString());
                        if (studentDay != null) {
                            Log.d("CIS 3334", "getStudentAPI: " + response.toString());
                            textViewStatus.setText(studentDay.getData());
                        } else {
                            textViewStatus.setText("Parse error: CurrencyRate was null");
                        }
                    } catch (IOException e) {
                        textViewStatus.setText("Parse exception: " + e.getMessage());
                    }
                },
                error -> textViewStatus.setText("ERROR Response: " + error.toString())
        );

        mRequestQueue.add(jsonObjectRequest);
    }

    private void getDogFact() {
        String url = "https://dogapi.dog/api/facts";

        @SuppressLint("SetTextI18n") JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    Moshi moshi = new Moshi.Builder().build();
                    JsonAdapter<DogFact> adapter = moshi.adapter(DogFact.class);
                    try {
                        DogFact dogFact = adapter.fromJson(response.toString());
                        if (dogFact != null) {
                            textViewStatus.setText(dogFact.getFirstFact());
                        } else {
                            textViewStatus.setText("Parse error: DogFact was null");
                        }
                    } catch (IOException e) {
                        textViewStatus.setText("Parse exception: " + e.getMessage());
                    }
                },
                error -> textViewStatus.setText("ERROR Response: " + error.toString())
        );

        mRequestQueue.add(jsonObjectRequest);
    }


    // Get current weather conditions from OpenWeather (example URL below)
    // https://api.openweathermap.org/data/2.5/weather?q=Duluth&units=imperial&appid=5aa6c40803fbb300fe98c6728bdafce7
    private void getWeather() {
        String url = "https://api.openweathermap.org/data/2.5/weather?q=Duluth&units=imperial&appid=5aa6c40803fbb300fe98c6728bdafce7";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        Moshi moshi = new Moshi.Builder().build();
                        JsonAdapter<OpenWeather> adapter = moshi.adapter(OpenWeather.class);
                        try {
                            OpenWeather obj = adapter.fromJson(response.toString());
                            if (obj != null && obj.getTemp() != null) {
                                textViewStatus.setText("Temperature is " + obj.getTemp().toString());
                            } else {
                                textViewStatus.setText("Parse error: OpenWeather/Temp missing");
                            }
                        } catch (IOException e) {
                            textViewStatus.setText("Parse exception: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        textViewStatus.setText("ERROR Response: " + error.toString());
                    }
                });

        mRequestQueue.add(jsonObjectRequest);
    }

    // Space news articles list
    private void getSpaceNews() {
        String url = "https://api.spaceflightnewsapi.net/v4/articles/?limit=10&offset=0";

        @SuppressLint("SetTextI18n") JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        // v4 returns an object with a "results" array
                        JSONArray results = response.getJSONArray("results");

                        textViewStatus.setText("Space News (latest " + results.length() + "):\n");

                        Moshi moshi = new Moshi.Builder().build();
                        JsonAdapter<SpaceNews> adapter = moshi.adapter(SpaceNews.class);

                        for (int i = 0; i < results.length(); i++) {
                            // Parse each article object
                            SpaceNews article = adapter.fromJson(results.getJSONObject(i).toString());
                            if (article != null) {
                                // Display title + summary (trim if long)
                                String summary = article.summary == null ? "" : article.summary.trim();
                                if (summary.length() > 240) summary = summary.substring(0, 240) + "…";
                                textViewStatus.append(
                                        "• " + article.title + "\n" +
                                                "   " + article.news_site + "  •  " + article.published_at + "\n" +
                                                "   " + summary + "\n" +
                                                "   " + article.url + "\n" +
                                                "==================\n"
                                );
                            } else {
                                textViewStatus.append("Parse error on article " + i + "\n==================\n");
                            }
                        }
                    } catch (JSONException | IOException e) {
                        textViewStatus.setText("Parse/JSON error: " + e.getMessage());
                    }
                },
                error -> {
                    // Make Volley errors more informative (status code & body if available)
                    String msg = "Volley error: " + error.toString();
                    if (error.networkResponse != null) {
                        msg += " (HTTP " + error.networkResponse.statusCode + ")";
                        try {
                            msg += " " + new String(error.networkResponse.data, "UTF-8");
                        } catch (Exception ignored) {}
                    }
                    textViewStatus.setText(msg);
                    Log.d("CIS 3334", "getSpaceNews error", error);
                }
        );

        mRequestQueue.add(jsonObjectRequest);
    }

    public void getCurrency() {
        String url = "https://api.fxratesapi.com/latest";

        @SuppressLint("SetTextI18n") JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    Moshi moshi = new Moshi.Builder().build();
                    JsonAdapter<CurrencyRate> adapter = moshi.adapter(CurrencyRate.class);
                    try {
                        CurrencyRate currencyRate = adapter.fromJson(response.toString());
                        if (currencyRate != null) {
                            textViewStatus.setText(currencyRate.getDescription());
                        } else {
                            textViewStatus.setText("Parse error: CurrencyRate was null");
                        }
                    } catch (IOException e) {
                        textViewStatus.setText("Parse exception: " + e.getMessage());
                    }
                },
                error -> textViewStatus.setText("ERROR Response: " + error.toString())
        );

        mRequestQueue.add(jsonObjectRequest);
    }

    private void setupButtonDogFact() {
        buttonGetFact = findViewById(R.id.buttonGetFact); // reuse same button id
        buttonGetFact.setOnClickListener(v -> {
            Log.d("CIS 3334", "getDogFact onClick");
            getDogFact();
        });
    }

    private void setupButtonCurrency() {
        buttonCurrency = findViewById(R.id.buttonCurrency);
        buttonCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CIS 3334", "getCurrency onClick");
                // Trigger async fetch; UI will update via LiveData observers
                getCurrency();
            }
        });
    }

    private void setupButtonWeather() {
        buttonWeather = findViewById(R.id.buttonWeather);
        buttonWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CIS 3334", "getWeather onClick");
                getWeather();
            }
        });
    }

    private void setupButtonSpaceNews() {
        buttonSpaceNews = findViewById(R.id.buttonSpaceNews);
        buttonSpaceNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CIS 3334", "getSpaceNews onClick");
                getSpaceNews();
            }
        });
    }

    private void setupButtonStudentAPI() {
        buttonStudentAPI = findViewById(R.id.buttonStudentAPI);
        buttonStudentAPI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CIS 3334", "getStudentAPI onClick");
                getStudentAPI();
            }
        });
    }
}
