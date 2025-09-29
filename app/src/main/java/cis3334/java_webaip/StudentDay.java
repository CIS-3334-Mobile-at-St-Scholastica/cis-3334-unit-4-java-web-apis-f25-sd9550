// API for historical events on the current day
package cis3334.java_webaip;

import android.util.Log;

import java.util.List;
import java.util.Map;

public class StudentDay {
    public String info;
    public String date;
    public String updated;
    public Data data;

    public static class Data {
        public List<Event> Events;  // remember that capitals matter!
        public List<Birth> Births;
        public List<Death> Deaths;

        public static class Event {
            public String text;
            public String html;
            public Map<String, Map<String, String>> links;
        }

        public static class Birth {
            public String text;
            public String html;
            public Map<String, Map<String, String>> links;
        }

        public static class Death {
            public String text;
            public String html;
            public Map<String, Map<String, String>> links;
        }
    }

    public String getData() {
        // skip deaths for now because depressing
        Log.d("CIS 3334", "getData() called - date: " + date);

        if (data == null) {
            return "No data available for " + date;
        }

        StringBuilder result = new StringBuilder();
        result.append("On ").append(date).append(":\n\n");

        if (data.Events != null && !data.Events.isEmpty()) {
            result.append("Events:\n");
            for (Data.Event event : data.Events) {
                result.append("• ").append(event.text).append("\n\n");
            }
        }

        if (data.Births != null && !data.Births.isEmpty()) {
            result.append("Births:\n");
            for (Data.Birth birth : data.Births) {
                result.append("• ").append(birth.text).append("\n\n");
            }
        }

        return result.toString();
    }
}
