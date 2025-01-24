package managers;

import collection.Event;
import collection.Ticket;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.Objects;
import java.util.Properties;
public class DataBaseManager {
    private static DataBaseManager db;
    private Connection connection;
    {
        try {
            DriverManager.registerDriver( new org.postgresql.Driver());
        } catch (SQLException e) {
            System.out.println("Драйвер отсутствует");
            throw new RuntimeException(e);
        }
    }
    private DataBaseManager(){
        try{
            Properties info = new Properties();
            info.load(new FileInputStream("pg.txt"));
            System.out.println("Выполнено подключение к базе " + info.getProperty("url"));
            this.connection = DriverManager.getConnection(info.getProperty("url"),info);
        } catch (SQLException exc) {
            System.out.println("Проблемочки с подключением к БД");
            throw new RuntimeException(exc);
        }catch (FileNotFoundException exc){
            System.out.println("конфиг pg.txt не найден");
            throw new RuntimeException(exc);
        } catch (Exception exc){
            throw new RuntimeException(exc);
        }
    }
    public static DataBaseManager getInstance(){
        if (Objects.isNull(db)){
            db = new DataBaseManager();
        }
        return db;
    }

    public void addUser(String login, String password) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Users(name,password) VALUES(?,?)");
        preparedStatement.setString(1,login);
        preparedStatement.setString(2,password);
        preparedStatement.execute();
    }

    public void addTicket(Ticket ticket) throws SQLException{
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO TicketCollection(id,name,coordinatesx,coordinatesy,creationdate,price,discount,tickettype,eventid,eventname,eventticketscount,eventtype,username) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)");

        preparedStatement.setLong(1,ticket.getId());
        preparedStatement.setString(2,ticket.getName());
        preparedStatement.setDouble(3,ticket.getCoordinates().getX());
        preparedStatement.setInt(4,ticket.getCoordinates().getY());
        preparedStatement.setDate(5, Date.valueOf(ticket.getCreationDate()));
        preparedStatement.setDouble(6,ticket.getPrice());
        preparedStatement.setFloat(7,ticket.getDiscount());
        preparedStatement.setString(8,ticket.getType().toString());

        Event event = ticket.getEvent();
        preparedStatement.setLong(9,event.getId());
        preparedStatement.setString(10,event.getName());
        preparedStatement.setLong(11,event.getTicketsCount());
        preparedStatement.setString(12,event.getEventType().toString());
        preparedStatement.setString(13,ticket.getUserName());
        preparedStatement.execute();
    }
    public void updateById(Long id, Ticket ticket) throws SQLException {
        try {

            String query = "UPDATE ticketcollection" +
                    "SET" +
                    "   name = ?," +
                    "   coordinates_x = ?," +
                    "   coordinates_y = ?," +
                    "   creationDate = ?," +
                    "   price = ?," +
                    "   discount = ?," +
                    "   ticketType = ?," +
                    "   eventId = ?," +
                    "   eventName = ?," +
                    "   eventTicketsCount = ?," +
                    "   eventType = ?," +
                    "   userName = ?," +
                    "WHERE id = ?";

            connection.setAutoCommit(false);

            PreparedStatement preparedStatement = connection.prepareStatement(query);


            preparedStatement.setString(2,ticket.getName());
            preparedStatement.setDouble(3,ticket.getCoordinates().getX());
            preparedStatement.setInt(4,ticket.getCoordinates().getY());
            preparedStatement.setDate(5, Date.valueOf(ticket.getCreationDate()));
            preparedStatement.setDouble(6,ticket.getPrice());
            preparedStatement.setFloat(7,ticket.getDiscount());
            preparedStatement.setString(8,ticket.getType().toString());

            Event event = ticket.getEvent();
            preparedStatement.setLong(9,event.getId());
            preparedStatement.setString(10,event.getName());
            preparedStatement.setLong(11,event.getTicketsCount());
            preparedStatement.setString(12,event.getEventType().toString());
            preparedStatement.setString(11,ticket.getUserName());
            preparedStatement.setLong(12,ticket.getId());

            connection.commit();
        }catch (SQLException sqlException){
            connection.rollback();
        }
    }
    public void removeById(Long id) throws SQLException {
        String sql = "DELETE FROM ticketCollection WHERE id = ?";
        connection.setAutoCommit(false);
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public void clear(String user) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM TicketCollection");
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            if (!resultSet.getString("userName").equals(user)){
                // Если есть хотя бы один тикет, не принадлежащий данному пользователю, выбрасываем исключение
                throw new SQLException("Не все объекты в коллекции принадлежат пользователю " + user);
            }
        }
        PreparedStatement deleteStatement = connection.prepareStatement("TRUNCATE TABLE TicketCollection");
        deleteStatement.execute();
    }
//    public void clear(String user) throws SQLException{
//        PreparedStatement preparedStatement = connection.prepareStatement("TRUNCATE TABLE TicketCollection");
//        preparedStatement.execute();
//    }
    public boolean isPasswordCorrect(String login,String password)  {
        try{
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT password FROM USERS WHERE name = ?"); // бросает ошибку => пользователя нет
            preparedStatement.setString(1, login);
            ResultSet resultSet = preparedStatement.executeQuery();
            String userPassword;
            if (resultSet.next()) {
                userPassword = resultSet.getString("password");
                return (password.equals(userPassword));
            }else{
                return false;
            }

        }catch (SQLException exc){
            System.out.println(exc.getMessage());
            return false;
        }


    }

    public boolean userExists(String name){
        try{
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT name FROM users where name = ?");
            preparedStatement.setString(1,name);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            return false;
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
