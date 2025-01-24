package command;


import app.ConsoleApp;
import collection.Ticket;
import connection.Server;
import managers.CollectionManager;

import java.sql.SQLException;

public class ClearCommand extends Command {

    public ClearCommand() {
        super("clear", "clear : очистить коллекцию");
    }

    @Override
    public void execute(String argument) {
        try {
            CollectionManager.clearData(Server.getUser().getName());
            Ticket.setIdCounter(0L);
            ConsoleApp.commandPrint("Коллекция очищена");
        } catch (SQLException e) {
            ConsoleApp.fail("Проблемы с бд");
            ConsoleApp.fail(e.getMessage());
        } catch (NullPointerException e){
            e.printStackTrace();
            ConsoleApp.fail("Проблемы с авторизацией, нужен релогин");
        }

    }
}
