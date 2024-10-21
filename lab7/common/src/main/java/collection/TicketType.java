package collection;

public enum TicketType {

    VIP("VIP"),
    USUAL("USUAL"),
    BUDGETARY("BUDGETARY"),
    CHEAP("CHEAP");

    private final String name;

    TicketType(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }
}