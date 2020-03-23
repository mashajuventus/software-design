package hw.test;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import hw.Master;
import hw.Request;

import java.sql.Time;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class Test {
    public static void main(String[] args) throws InterruptedException {
        Map<String, Boolean> isFailed = new HashMap<>();
        isFailed.put("bing", false);
        isFailed.put("google", false);
        isFailed.put("yandex", false);
        test(isFailed, "everything good test");

        isFailed.put("google", true);
        test(isFailed, "one problem test");
    }

    private static void test(Map<String, Boolean> isFailed, String testName) throws InterruptedException {
//        Map<String, Boolean> isFailed = new HashMap<>();
//        isFailed.put("bing", false);
//        isFailed.put("google", false);
//        isFailed.put("yandex", false);

        ActorSystem system = ActorSystem.create("MySystem");
        // Create actor
        ActorRef actor = system.actorOf(
                Props.create(Master.class, isFailed), "myActor");

        CompletableFuture<String> future = new CompletableFuture<>();
        // Send message
        actor.tell(new Request("ACTORS", future), ActorRef.noSender());
        try {
            String fromFuture = future.get();
            System.out.println(fromFuture);
            boolean found = false;
            List<String> possibleResults = (testName.equals("everything good test")) ? everythingGoodAnswer() : oneProblemAnswer();
            for (String possibleResult : possibleResults) {
                if (fromFuture.equals(possibleResult)) {
                    found = true;
                    break;
                }
            }
            if (found) {
                System.err.println(testName + " completed");
            } else {
                System.err.println(testName + "failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        system.terminate();
    }

    private static List<String> oneProblemAnswer() {
        List<String> answer = new ArrayList<>();
        answer.add("yandex results:\n" +
        "  result 1 consisting of ACTORS\n" +
        "  result 2 consisting of ACTORS\n" +
        "  result 3 consisting of ACTORS\n" +
        "  result 4 consisting of ACTORS\n" +
        "  result 5 consisting of ACTORS\n" +
        "bing results:\n" +
        "  result 1 consisting of ACTORS\n" +
        "  result 2 consisting of ACTORS\n" +
        "  result 3 consisting of ACTORS\n" +
        "  result 4 consisting of ACTORS\n" +
        "  result 5 consisting of ACTORS\n" +
        "No results from google\n");

        answer.add("bing results:\n" +
                "  result 1 consisting of ACTORS\n" +
                "  result 2 consisting of ACTORS\n" +
                "  result 3 consisting of ACTORS\n" +
                "  result 4 consisting of ACTORS\n" +
                "  result 5 consisting of ACTORS\n" +
                "yandex results:\n" +
                "  result 1 consisting of ACTORS\n" +
                "  result 2 consisting of ACTORS\n" +
                "  result 3 consisting of ACTORS\n" +
                "  result 4 consisting of ACTORS\n" +
                "  result 5 consisting of ACTORS\n" +
                "No results from google\n");
        return answer;
    }

    private static List<String> everythingGoodAnswer() {
        List<List<String>> ways = new ArrayList<>();
        ways.add(Arrays.asList("bing", "google", "yandex"));
        ways.add(Arrays.asList("bing", "yandex", "google"));
        ways.add(Arrays.asList("google", "bing", "yandex"));
        ways.add(Arrays.asList("google", "yandex", "bing"));
        ways.add(Arrays.asList("yandex", "bing", "google"));
        ways.add(Arrays.asList("yandex", "google", "bing"));

        StringBuilder part = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            part.append("  result ")
                    .append(i + 1)
                    .append(" consisting of ")
                    .append("ACTORS")
                    .append('\n');
        }

        List<String> answer = new ArrayList<>();
        for (List<String> way : ways) {
            StringBuilder builder = new StringBuilder();
            for (String searchSystem : way) {
                builder.append(searchSystem)
                        .append(" results:\n")
                        .append(part);

            }
            answer.add(builder.toString());
        }
        return answer;
    }

}
