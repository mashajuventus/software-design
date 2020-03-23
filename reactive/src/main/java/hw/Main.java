package hw;

import io.reactivex.netty.protocol.http.server.HttpServer;
import rx.Observable;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        HttpServer
                .newServer(8080)
                .start((req, resp) -> resp.writeString(server.request(req)))
                .awaitShutdown();

    }
}
