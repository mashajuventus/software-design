package hw;

import akka.actor.*;
import scala.concurrent.duration.Duration;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class Master extends UntypedActor {

    public CompletableFuture<String> future;
    public List<String> searchSystems = Arrays.asList("google", "yandex", "bing");
    public List<Response> responses = new ArrayList<>();
    public Map<String, Boolean> hasAnswer = fillFalse();
    public Map<String, Boolean> isFailed;

    public Master(Map<String, Boolean> isFailed) {
        this.isFailed = isFailed;
    }

    @Override
    public void onReceive(Object o) throws Throwable {
        if (o instanceof Request) {
            future = ((Request) o).future;
            for (String searchSystem : searchSystems) {
                getContext().actorOf(Props.create(Child.class, isFailed.get(searchSystem)), searchSystem);
            }
            for (ActorRef actor : getContext().getChildren()) {
                actor.tell(((Request) o).request, self());
            }
            getContext().setReceiveTimeout(Duration.create("5 seconds"));
        } else if (o instanceof Response) {
            responses.add((Response) o);
            hasAnswer.put(((Response) o).searchSystem, true);
            if (responses.size() == searchSystems.size()) {
                collectResults();
            }
        } else if (o instanceof ReceiveTimeout) {
            collectResults();
        }
    }

    private void collectResults() {
        getContext().setReceiveTimeout(Duration.Undefined());
        StringBuilder builder = new StringBuilder();
        for (Response response : responses) {
            for (String oneResult : response.results) {
                builder.append(oneResult)
                        .append('\n');
            }
        }
        for (Map.Entry<String, Boolean> entry : hasAnswer.entrySet()) {
            if (!entry.getValue()) {
                builder.append(generateNoAnswer(entry.getKey()));
            }
        }
        future.complete(builder.toString());
        context().stop(self());
    }

    private Map<String, Boolean> fillFalse() {
        Map<String, Boolean> answer = new HashMap<>();
        for (String searchSystem : searchSystems) {
            answer.put(searchSystem, false);
        }
        return answer;
    }

    private String generateNoAnswer(String searchSystem) {
        return "No results from " + searchSystem + '\n';
    }
}
