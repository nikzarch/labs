package dto;

import collection.Ticket;
import util.User;

import java.io.Serializable;

public class Request implements Serializable {
    private static final long serialVersionUID = 1234599L;
    private final String commandName;
    private final String commandArgument;
    private Ticket objectArgument;

    private User user;

    public Request(String commandName, String commandArgument) {
        this.commandName = commandName;
        this.commandArgument = commandArgument;
    }
    public Request(String commandName, String commandArgument, User user) {
        this.commandName = commandName;
        this.commandArgument = commandArgument;
        this.user = user;
    }

    public Request(String commandName, String commandArgument, Ticket objectArgument) {
        this.commandName = commandName;
        this.commandArgument = commandArgument;
        this.objectArgument = objectArgument;
    }

    public Request(String commandName, String commandArgument, Ticket objectArgument, User user) {
        this.commandName = commandName;
        this.commandArgument = commandArgument;
        this.objectArgument = objectArgument;
        this.user = user;
    }

    public String getCommandArgument() {
        return commandArgument;
    }

    public String getCommandName() {
        return commandName;
    }

    public Ticket getObjectArgument() {
        return objectArgument;
    }

    public User getUser(){ return user;}
}
