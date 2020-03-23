package hw;

import akka.actor.UntypedActor;

import java.util.concurrent.TimeUnit;

public class Child extends UntypedActor {
    private boolean isFailed;

    public Child(boolean isFailed) {
        this.isFailed = isFailed;
    }

    @Override
    public void onReceive(Object o) throws Throwable {
        if (o instanceof String) {
            String searchSystem = self().path().name();
            String result = RealSearcher.generateResults(searchSystem, (String) o);
            if (isFailed) {
                TimeUnit.SECONDS.sleep(10);
            }
            sender().tell(new Response(searchSystem, result), self());
        }
    }
}
