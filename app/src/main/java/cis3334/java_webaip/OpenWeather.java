package cis3334.java_webaip;

import java.util.List;

// Java model for OpenWeather current weather JSON.
// Key point: nested classes must be static for Moshi to serialize/deserialize.
public class OpenWeather {

    public static class Coord {
        public double lon;
        public double lat;
    }

    public static class Weather {
        public int id;
        public String main;
        public String description;
        public String icon;
    }

    public static class Main {
        public double temp;
        public double feels_like;
        public double temp_min;
        public double temp_max;
        public int pressure;
        public int humidity;

        // Optional fields often present; Moshi will ignore if absent.
        public Integer sea_level;
        public Integer grnd_level;
    }

    public static class Wind {
        public double speed;
        public int deg;
        // Optional gust sometimes present:
        public Double gust;
    }

    public static class Clouds {
        public int all;
    }

    public static class Sys {
        public int type;     // sometimes omitted
        public int id;       // sometimes omitted
        public String country;
        public int sunrise;  // fits in 32-bit for now; could switch to long if you prefer
        public int sunset;
    }

    public Coord coord;
    public List<Weather> weather;
    public String base;
    public Main main;
    public int visibility;
    public Wind wind;
    public Clouds clouds;
    public int dt;
    public Sys sys;
    public int timezone;
    public int id;
    public String name;
    public int cod;

    // --- Convenience accessors used by your UI ---

    public Double getTemp() {
        return (main != null) ? main.temp : null;
    }

    public String getDescription() {
        if (weather != null && !weather.isEmpty() && weather.get(0) != null) {
            return weather.get(0).description;
        }
        return null;
    }
}
