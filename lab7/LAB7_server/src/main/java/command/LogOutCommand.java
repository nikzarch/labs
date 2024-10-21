package command;

import app.ConsoleApp;
import connection.Server;
import util.User;

public class LogOutCommand extends Command{

    public LogOutCommand(){
        super("logout","logout : выйти из учетной записи");
    }

    @Override
    public void execute(String argument) {
        ConsoleApp.commandPrint("Вы разлогинились");
        Server.setUser(new User("unauthorized",""));
    }
}
