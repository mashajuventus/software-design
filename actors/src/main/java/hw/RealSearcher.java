package hw;

public class RealSearcher {
    public static String generateResults(String searchSystem, String request) {
        StringBuilder builder = new StringBuilder();
        builder.append(searchSystem).append(" results:\n");
        for (int i = 0; i < 5; i++) {
            builder.append("  result ")
                    .append(i + 1)
                    .append(" consisting of ")
                    .append(request)
                    .append('\n');
        }
        return builder.toString();
    }
}
