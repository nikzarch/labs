package managers;

import app.ConsoleApp;
import collection.Coordinates;
import collection.Event;
import collection.EventType;
import collection.Ticket;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class CollectionManager {
    private static final List<Ticket> collection = Collections.synchronizedList(new ArrayList<>());

    public static List<Ticket> getCollection() {
        return collection;
    }

    public static void addTicket(Ticket ticket) throws SQLException {
        ticket.setId(Ticket.getIdCounter() + 1);
        Ticket.setIdCounter(Ticket.getIdCounter() + 1);
        DataBaseManager.getInstance().addTicket(ticket);
        collection.add(ticket);
    }


    public static void updateTicket(Long id, Ticket ticket) {
        try {
            DataBaseManager.getInstance().updateById(id, ticket);
            setTicket(id, ticket);
        } catch (SQLException exc) {
            ConsoleApp.fail("Не удалось обновить, что-то с БД");
        }

    }

    public static void removeTicketById(Long id) throws SQLException {
        boolean noId = true;
        DataBaseManager.getInstance().removeById(id);
        synchronized (collection) {
            for (int i = 0; i < collection.size(); i++) {
                if (collection.get(i).getId().equals(id)) {
                    collection.remove(i);
                    try {
                        ArrayList<Ticket> secondCollection = new ArrayList<>(collection);
                        Collections.sort(secondCollection);
                        Ticket.setIdCounter(secondCollection.isEmpty() ? 0L : secondCollection.get(secondCollection.size() - 1).getId());
                    } catch (Exception exc) {
                        Ticket.setIdCounter(0L);
                    }
                    noId = false;
                    break;
                }
            }
        }
        if (noId) {
            ConsoleApp.fail("Айди не существует");
        }
    }


    public static void ticketExistsAndCanBeUpdated(int id, String userName) {
        boolean ticketExists = false;
        synchronized (collection) {
            for (Ticket i : collection) {
                if (i.getId() == id) {
                    ticketExists = true;
                    break;
                }
            }
        }
        System.out.println(ticketExists);
        if (ticketExists && getTicket(id).getUserName().equals(userName)) {
            ConsoleApp.commandPrint("Айди существует и доступно настоящему пользователю");
        } else {
            ConsoleApp.fail("Айди не существует или у вас нет прав доступа");
        }
    }

    public static void clearData(String user) throws SQLException {
        DataBaseManager.getInstance().clear(user);
        collection.clear();
    }


    public static Ticket getTicket(long number) {
        synchronized (collection) {
            for (Ticket i : collection) {
                if (i.getId() == number) {
                    return i;
                }
            }
        }
        return null;
    }


    public static void setTicket(long number, Ticket ticket) {
        synchronized (collection) { // синхронизируем, так как тут должна быть атомарная операция, а синх коллекция обеспечивает потокобезопасность только в рамках "встроенных" методов( collection.set)
            for (int i = 0; i < collection.size(); i++) {
                if (collection.get(i).getId() == number) {
                    collection.set(i, ticket);
                    break;
                }
            }
        }
    }

    public static void initialize() throws SQLException {
        Connection connection = DataBaseManager.getInstance().getConnection();
        PreparedStatement ticketsStatement = connection.prepareStatement("SELECT * FROM TicketCollection ORDER BY id ASC");
        ResultSet ticketsResult = ticketsStatement.executeQuery();
        while (ticketsResult.next()) {
            Ticket ticket = new Ticket();
            ticket.setId(ticketsResult.getLong("id"));
            ticket.setName(ticketsResult.getString("name"));
            ticket.setCoordinates(new Coordinates(ticketsResult.getDouble("coordinatesX"), ticketsResult.getInt("coordinatesY")));
            ticket.setCreationDate(ticketsResult.getDate("creationDate").toLocalDate());
            ticket.setPrice(ticketsResult.getDouble("price"));
            ticket.setDiscount((float) ticketsResult.getDouble("discount"));
            ticket.setType(ticketsResult.getString("ticketType"));
            ticket.setEvent(new Event(ticketsResult.getInt("eventId"), ticketsResult.getString("eventName"), ticketsResult.getLong("eventTicketsCount"), EventType.valueOf(ticketsResult.getString("eventType"))));
            ticket.setUserName(ticketsResult.getString("userName"));
            collection.add(ticket);
        }
    }
}
