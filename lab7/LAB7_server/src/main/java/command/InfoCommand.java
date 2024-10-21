package command;


import app.ConsoleApp;
import collection.Ticket;
import managers.CollectionManager;

import java.util.ArrayList;
import java.util.List;

public class InfoCommand extends Command {

    public InfoCommand() {
        super("info", "info : вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)");
    }

    @Override
    public void execute(String argument) {
        List<Ticket> arr = CollectionManager.getCollection();
        ConsoleApp.commandPrint("Тип коллекции " + ArrayList.class.getName() + ", Количество элементов: " + arr.size());
    }
}
