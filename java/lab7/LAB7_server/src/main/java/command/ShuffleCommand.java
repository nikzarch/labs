package command;

import collection.Ticket;
import managers.CollectionManager;

import java.util.Collections;
import java.util.List;

public class ShuffleCommand extends Command {

    public ShuffleCommand() {
        super("shuffle", "shuffle : перемешать элементы коллекции в случайном порядке");
    }

    @Override
    public void execute(String argument) {
        List<Ticket> arr = CollectionManager.getCollection();
        Collections.shuffle(arr);
    }

}
