package app.domain.models;

public class Item {

    private final  long id;
    private final  String value;

    public Item(long id, String value) {
        this.id = id;
        this.value = value;
    }

    public long getId() {
        return id;
    }

    public String getValue() {
        return value;
    }
}
