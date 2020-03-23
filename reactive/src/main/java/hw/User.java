package hw;

import org.bson.Document;

public class User {
    public final int id;
    public final String currency;
    public final String login;


    public User(Document doc) {
        this(doc.getDouble("id").intValue(), doc.getString("currency"), doc.getString("login"));
    }

    public User(int id, String currency, String login) {
        this.id = id;
        this.currency = currency;
        this.login = login;
    }

    public Document toDocument() {
        return new Document()
                .append("id", id)
                .append("currency", currency)
                .append("login", login);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", currency='" + currency + '\'' +
                ", login='" + login + '\'' +
                '}';
    }
}

