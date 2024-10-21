package command;

import dto.Request;
import util.User;
import app.ConsoleApp;

public class AddCommand extends Command{

    public AddCommand() {
        super("add");
    }

    @Override
    public Request execute(String argument, User user) {
        Request request = new Request(super.name, argument, ConsoleApp.formTicketToSend(user), user);
        return request;
    }
}
