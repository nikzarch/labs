package command;

import app.ConsoleApp;
import collection.Ticket;
import managers.CollectionManager;

import java.util.List;

public class FilterStartsWithNameCommand extends Command {

    public FilterStartsWithNameCommand() {
        super("filter_starts_with_name", "filter_starts_with_name name : вывести элементы, значение поля name которых начинается с заданной подстроки");
    }

    @Override
    public void execute(String argument) {
        List<Ticket> arr = CollectionManager.getCollection();
        for (int i = 0; i < arr.size(); i++) {
            if (arr.get(i).getName().startsWith(argument)) {
                ConsoleApp.commandPrint(arr.get(i).toString());
            }
        }
    }
}
