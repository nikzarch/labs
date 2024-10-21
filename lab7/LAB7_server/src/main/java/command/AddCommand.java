package command;


import app.ConsoleApp;
import collection.Ticket;
import managers.CollectionManager;

import java.sql.SQLException;

public class AddCommand extends Command {

    public AddCommand() {
        super("add", "add : добавить объект");
    }


    public void execute(Ticket objectArgument) {

        try {
            CollectionManager.addTicket(objectArgument);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            ConsoleApp.fail("Не удалось добавить, проблемы с бдшкой");
        }
    }

    @Override
    public void execute(String argument) {
    }
}
