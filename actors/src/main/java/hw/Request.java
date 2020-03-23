package hw;

import java.util.concurrent.CompletableFuture;

public class Request {
    public String request;
    public CompletableFuture<String> future;

    public Request(String request, CompletableFuture<String> future) {
        this.request = request;
        this.future = future;
    }
}
