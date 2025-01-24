package command;

import app.ConsoleApp;
import dto.Request;
import util.User;

public class UpdateCommand extends Command{

    public UpdateCommand() {
        super("update");
    }

    @Override
    public Request execute(String argument, User user) throws ExecutionFailedCommandException {
        if (ConsoleApp.getInstance().checkIdForUpdate(new Request(super.name,argument,user))){
            return new Request(super.name, argument, ConsoleApp.formTicketToSend(user), user);
        }else{
            throw new ExecutionFailedCommandException("Не удалось выполнить команду");
        }
    }
}
