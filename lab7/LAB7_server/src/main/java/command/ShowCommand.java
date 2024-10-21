package command;


import app.ConsoleApp;
import collection.Ticket;
import managers.CollectionManager;

import java.util.List;

public class ShowCommand extends Command {

    public ShowCommand() {
        super("show", "show : вывести в стандартный поток вывода все элементы коллекции в строковом представлении");
    }

    @Override
    public void execute(String argument) {
        List<Ticket> arr = CollectionManager.getCollection();
        if (arr.isEmpty()) {
            ConsoleApp.commandPrint("Коллекция пуста");
        }
        for (int i = 0; i < arr.size(); i++) {
            ConsoleApp.commandPrint(arr.get(i).toString() + " ");
        }
    }
}
