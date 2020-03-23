package hw;

import org.bson.Document;

public class Good {
    public final int id;
    public final String name;
    public final String currency;
    public final Double price;


    public Good(Document doc) {
        this(doc.getDouble("id").intValue(), doc.getString("name"), doc.getString("currency"), doc.getDouble("price"));
    }

    public Good(int id, String name, String currency, Double price) {
        this.id = id;
        this.name = name;
        this.currency = currency;
        this.price = price;
    }

    public Document toDocument() {
        return new Document()
                .append("id", id)
                .append("name", name)
                .append("currency", currency)
                .append("price", price);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name=" + name + '\'' +
                ", currency='" + currency + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}
