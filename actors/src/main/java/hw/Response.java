package hw;

import java.util.ArrayList;
import java.util.List;

public class Response {
    public String searchSystem;
    public List<String> results;

    public Response(String searchSystem, String answer) {
        this.searchSystem = searchSystem;
        this.results = parse(answer);
    }

    private List<String> parse(String answer) {
        String[] parsed = answer.split("\n");
        List<String> result = new ArrayList<>();
        for (int i = 0; i < parsed.length; i++) {
            result.add(parsed[i]);
        }
        return result;
    }
}
