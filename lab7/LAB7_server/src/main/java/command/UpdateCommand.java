package command;


import app.ConsoleApp;
import collection.Ticket;
import managers.CollectionManager;

public class UpdateCommand extends Command {

    public UpdateCommand() {
        super("update", "update id {element} : обновить значение элемента коллекции, id которого равен заданному");
    }

    @Override
    public void execute(String argument) {

    }



}
