package command;

import collection.Ticket;
import managers.CollectionManager;

import java.util.Collections;
import java.util.List;

public class ReorderCommand extends Command {

    public ReorderCommand() {
        super("reorder", "reorder : отсортировать коллекцию в порядке, обратном нынешнему");
    }

    @Override
    public void execute(String argument) {
        List<Ticket> arr = CollectionManager.getCollection();
        Collections.reverse(arr);
    }
}
