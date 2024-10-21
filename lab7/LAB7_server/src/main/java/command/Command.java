package command;


import collection.Ticket;

import java.util.HashMap;

public abstract class Command {
    public static HashMap<String, Command> commandList = new HashMap<>();

    static {
        commandList.put("help", new HelpCommand());
        commandList.put("info", new InfoCommand());
        commandList.put("show", new ShowCommand());
        commandList.put("add", new AddCommand());
        commandList.put("update", new UpdateCommand());
        commandList.put("remove_by_id", new RemoveByIdCommand());
        commandList.put("clear", new ClearCommand());
        commandList.put("exit", new ExitCommand());
        commandList.put("shuffle", new ShuffleCommand());
        commandList.put("reorder", new ReorderCommand());
        commandList.put("filter_by_discount", new FilterByDiscountCommand());
        commandList.put("filter_starts_with_name", new FilterStartsWithNameCommand());
        commandList.put("print_field_descending_discount", new PrintFieldDescendingDiscountCommand());
        commandList.put("register", new RegisterCommand());
        commandList.put("login", new LoginCommand());
        commandList.put("logout", new LogOutCommand());
    }

    protected String helpInfo;
    protected String name;

    public Command(String name, String helpInfo) {
        this.name = name;
        this.helpInfo = helpInfo;
    }

    public abstract void execute(String argument);

    public String getName() {
        return this.name;
    }

    public String getHelpInfo() {
        return helpInfo;
    }

    public void execute(Ticket objectArgument) {
    }

    public void execute(String argument, String userName) {
    }

}
