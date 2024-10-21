package collection;

import com.fasterxml.jackson.annotation.JsonSetter;

import java.io.Serializable;
import java.time.LocalDate;

public class Ticket implements Comparable<Ticket>, Serializable {
    private static Long idCounter = 0L;
    private Long id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private LocalDate creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Double price; //Поле не может быть null, Значение поля должно быть больше 0
    private Float discount; //Поле может быть null, Значение поля должно быть больше 0, Максимальное значение поля: 100
    private TicketType type; //Поле может быть null
    private Event event; //Поле не может быть null
    private String userName;

    public Ticket() {
        this.id = ++idCounter;
        this.creationDate = LocalDate.now();
        this.coordinates = new Coordinates();
        this.event = new Event();
    }

    public Ticket(Long id, String name, Coordinates coordinates, Double price, Float discount, TicketType ticketType, Event event) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = LocalDate.now();
        this.price = price;
        this.type = ticketType;
        this.discount = discount;
        this.event = event;
    }

    public static long getIdCounter() {
        return idCounter;
    }

    public static void setIdCounter(Long idCounter) {
        Ticket.idCounter = idCounter;
    }

    public int compareTo(Ticket t) {
        if (this.id > t.getId()) {
            return 1;
        } else if (this.id < t.getId()) {
            return -1;
        } else {
            return 0;
        }
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Coordinates getCoordinates() {
        return this.coordinates;
    }

    @JsonSetter // помечаем конкретный сеттер для корректной десериализации
    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public void setCoordinatesX(String coordinates) {
        coordinates = coordinates.strip().replace(',', '.');
        this.coordinates.setX(Double.parseDouble(coordinates));
    }

    public void setCoordinatesY(String coordinates) {
        coordinates = coordinates.strip().replace(',', '.');
        this.coordinates.setY(Integer.parseInt(coordinates));
    }

    public LocalDate getCreationDate() {
        return this.creationDate;
    }

    @JsonSetter // помечаем конкретный сеттер для корректной десериализации
    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }


    public Double getPrice() {
        return this.price;
    }

    public void setPrice(String price) {
        Double p = Double.parseDouble(price.replace(',', '.'));
        this.price = p;
    }

    @JsonSetter // помечаем конкретный сеттер для корректной десериализации
    public void setPrice(Double price) {
        this.price = price;
    }

    public Float getDiscount() {
        return this.discount;
    }

    @JsonSetter // помечаем конкретный сеттер для корректной десериализации
    public void setDiscount(Float discount) {
        this.discount = discount;

    }

    public void setDiscount(String disc) {
        Float discount = Float.parseFloat(disc.replace(',', '.'));
        this.discount = discount;
    }

    public TicketType getType() {
        return this.type;
    }

    @JsonSetter // помечаем конкретный сеттер для корректной десериализации
    public void setType(TicketType ticketType) {
        this.type = ticketType;
    }

    public void setType(String input) {
        try {
            Integer intTicketType = Integer.parseInt(input); // приводим к инту, если не приводится ловим ошибку и задаём строкой
            this.type = TicketType.values()[intTicketType - 1];
        } catch (NumberFormatException exc) {
            this.type = TicketType.valueOf(input);
        }
    }

    public Event getEvent() {
        return this.event;
    }

    @JsonSetter // помечаем конкретный сеттер для корректной десериализации
    public void setEvent(Event event) {
        this.event = event;
    }

    @Override
    public String toString() {
        return "Билет " + this.name + " №" + this.id + ", тип: " + this.type.toString() + ", дата создания: " + this.creationDate.toString() + ", цена: " + this.price + " рублей, скидка: " + this.discount + "%, на мероприятие " + this.event.toString() +", создатель: " + this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }
}