package collection;

import com.fasterxml.jackson.annotation.JsonSetter;

import java.io.Serializable;

import static java.lang.Math.max;

public class Event implements Serializable {
    private static Integer idCounter = 0;
    private Integer id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Long ticketsCount; //Поле может быть null, Значение поля должно быть больше 0
    private EventType eventType; //Поле не может быть null

    public Event() {
        this.id = idCounter + 1;
        idCounter++;
    }

    public Event(String name, Long ticketsCount, EventType eventType) {
        this.id = idCounter + 1;
        idCounter++;
        this.name = name;
        this.ticketsCount = ticketsCount;
        this.eventType = eventType;
    }

    public Event(int eventId, String eventName, Long eventTicketsCount, EventType eventType) {
        this.id = eventId;
        idCounter = max(eventId,idCounter);
        this.name = eventName;
        this.ticketsCount = eventTicketsCount;
        this.eventType = eventType;
    }

    public static void incrementIdCounter() {
        idCounter++;
    }

    public static Integer getIdCounter() {
        return idCounter;
    }

    @JsonSetter // помечаем конкретный сеттер для корректной десериализации
    public static void setIdCounter(int i) {
        idCounter = i;
    }

    public Integer getId() {
        return id;
    }

    @JsonSetter // помечаем конкретный сеттер для корректной десериализации
    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTicketsCount() {
        return ticketsCount;
    }

    @JsonSetter // помечаем конкретный сеттер для корректной десериализации
    public void setTicketsCount(Long ticketsCount) {
        this.ticketsCount = ticketsCount;
    }

    public void setTicketsCount(String ticketsCount) {
        this.ticketsCount = Long.parseLong(ticketsCount);
    }

    public EventType getEventType() {
        return eventType;
    }

    @JsonSetter // помечаем конкретный сеттер для корректной десериализации
    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public void setEventType(String input) {
        try {
            Integer intEventType = Integer.parseInt(input); // приводим к инту, если не приводится ловим ошибку и задаём строкой
            this.eventType = EventType.values()[intEventType - 1];
        } catch (NumberFormatException exc) {
            this.eventType = EventType.valueOf(input);
        }
    }

    public String toString() {
        return "Мероприятие " + this.name + " id: " + this.id + " " + ", Количество билетов: " + this.ticketsCount + ", Тип мероприятия: " + this.eventType.toString();
    }
}