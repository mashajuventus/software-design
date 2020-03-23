package hw;

import com.mongodb.client.model.Filters;
import com.mongodb.rx.client.MongoClients;
import com.mongodb.rx.client.MongoDatabase;
import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import rx.Observable;

import java.util.List;
import java.util.Map;

public class Server {
    public Server() {
    }

    public static MongoDatabase client = MongoClients.create("mongodb://localhost:27017").getDatabase("rxjava");

    public Observable<String> request(HttpServerRequest<ByteBuf> req) {
        String name = req.getDecodedPath().substring(1);
        Map<String, List<String>> parameters = req.getQueryParameters();
        if (name.equals("add-user")) {
            User user = new User(Integer.parseInt(parameters.get("id").get(0)),
                    parameters.get("currency").get(0),
                    parameters.get("login").get(0));
            return client.getCollection("users")
                    .insertOne(user.toDocument())
                    .asObservable()
                    .map(Object::toString);
        } else if (name.equals("add-good")) {
            Good good = new Good(Integer.parseInt(parameters.get("id").get(0)),
                    parameters.get("name").get(0),
                    parameters.get("currency").get(0),
                    Double.parseDouble(parameters.get("price").get(0)));
            return client.getCollection("goods")
                    .insertOne(good.toDocument())
                    .asObservable()
                    .map(Object::toString);
        } else if (name.equals("show")) {
            int id = Integer.parseInt(parameters.get("id").get(0));
            return client.getCollection("users")
                    .find(Filters.eq("id", id))
                    .toObservable()
                    .map(User::new)
                    .single()
                    .flatMap(user -> client.getCollection("goods")
                            .find()
                            .toObservable()
                            .map(Good::new)
                            .map(good -> good.id + " " +
                                    good.name + " " +
                                    good.price * Bank.convert(good.currency, user.currency) + " " +
                                    user.currency));
        } else {
            return Observable.just("Unknown request " + name);
        }
    }

}
