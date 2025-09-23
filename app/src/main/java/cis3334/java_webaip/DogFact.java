package cis3334.java_webaip;

import java.util.List;

public class DogFact {
    public List<String> facts;
    public boolean success;

    // Convenience getter
    public String getFirstFact() {
        if (facts != null && !facts.isEmpty()) {
            return facts.get(0);
        }
        return "No fact found";
    }
}