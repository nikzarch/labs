package command;

import app.ConsoleApp;
import collection.Ticket;
import collection.TicketDiscountComparator;
import managers.CollectionManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PrintFieldDescendingDiscountCommand extends Command {

    public PrintFieldDescendingDiscountCommand() {
        super("print_field_descending_discount_command", "print_field_descending_discount : вывести значения поля discount всех элементов в порядке убывания");
    }

    @Override
    public void execute(String argument) {
        List<Ticket> arrayList  = new ArrayList<>(CollectionManager.getCollection()) ;
        Comparator ticketDiscountComparator = new TicketDiscountComparator();
        Collections.sort(arrayList, ticketDiscountComparator);
        Collections.reverse(arrayList);
        for (int i = 0; i < arrayList.size(); i++) {
            ConsoleApp.commandPrint(arrayList.get(i).getDiscount().toString());
        }
    }
}
