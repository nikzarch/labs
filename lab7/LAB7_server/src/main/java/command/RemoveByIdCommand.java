package command;


import app.ConsoleApp;
import managers.CollectionManager;

import java.sql.SQLException;

public class RemoveByIdCommand extends Command {

    public RemoveByIdCommand() {
        super("remove_by_id", "remove_by_id id : удалить элемент из коллекции по его id");
    }

    @Override
    public void execute(String argument) {
        Long idToRemove = Long.parseLong(argument);
        try {
            CollectionManager.removeTicketById(idToRemove);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void execute(String argument, String userName){
        Long idToRemove = Long.parseLong(argument);
        if (CollectionManager.getTicket(idToRemove).getUserName().equals(userName)) {
            try {
                CollectionManager.removeTicketById(idToRemove);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                ConsoleApp.fail("Не удалось удалить билет");
            }
        }else{
            ConsoleApp.fail("Нет прав доступа");
        }
    }
}
