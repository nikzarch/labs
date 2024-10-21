package command;

import dto.Request;
import util.User;

import java.util.HashMap;


public class Command {
    public static HashMap<String,Command> commandList = new HashMap<>(); // сделаем через множество, потому что теперь используем командлист только для валидации команды{

    static {
        commandList.put("help", new Command("help"));
        commandList.put("info", new Command("info"));
        commandList.put("show", new Command("show"));
        commandList.put("add", new AddCommand());
        commandList.put("update", new UpdateCommand());
        commandList.put("remove_by_id", new Command("remove_by_id"));
        commandList.put("clear", new Command("clear"));
        commandList.put("exit", new Command("exit"));
        commandList.put("shuffle", new Command("shuffle"));
        commandList.put("reorder", new Command("reorder"));
        commandList.put("filter_by_discount", new Command("filter_by_discount"));
        commandList.put("filter_starts_with_name",new Command("filter_starts_with_name"));
        commandList.put("print_field_descending_discount", new Command("print_field_descending_discount"));
        commandList.put("logout", new Command("logout"));
    }

    protected String name;

    public Command(String name){
        this.name = name;
    }

    public Request execute(String argument, User user) throws ExecutionFailedCommandException {
        Request request = new Request(this.name,argument,user);
        return request;
    }

    public String getName() {
        return this.name;
    }

}
