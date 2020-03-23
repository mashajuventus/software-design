package hw;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class Main {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("MySystem");
        // Create actor
        ActorRef actor = system.actorOf(
                Props.create(Master.class), "myActor");

        // Send message
        actor.tell("request", ActorRef.noSender());
    }
}
