package hw;

import java.util.HashMap;
import java.util.Map;

public class Bank {

    public static double convert(String from, String to) {
        Map<String, Double> answer = new HashMap<>();
        answer.put("usd", 1.24);
        answer.put("eur", 1.15);
        answer.put("rub", 100.0);
        return answer.get(to) / answer.get(from);
    }
}
