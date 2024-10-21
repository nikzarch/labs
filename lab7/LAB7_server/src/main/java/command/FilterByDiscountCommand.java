package command;

import app.ConsoleApp;
import collection.Ticket;
import managers.CollectionManager;

import java.util.List;


public class FilterByDiscountCommand extends Command {

    public FilterByDiscountCommand() {
        super("filter_by_discount", "filter_by_discount discount : вывести элементы, значение поля discount которых равно заданному");
    }

    @Override
    public void execute(String argument) {
        Float discount = Float.parseFloat(argument);
        List<Ticket> arr = CollectionManager.getCollection();
        for (int i = 0; i < arr.size(); i++) {
            if (arr.get(i).getDiscount().equals(discount)) {
                ConsoleApp.commandPrint(arr.get(i).toString());
            }
        }
    }
}
