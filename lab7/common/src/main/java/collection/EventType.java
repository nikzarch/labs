package collection;

public enum EventType {
    CONCERT("CONCERT"),
    BASKETBALL("BASKETBALL"),
    THEATRE_PERFORMANCE("THEATRE_PERFORMANCE");
    private final String name;

    EventType(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }
}