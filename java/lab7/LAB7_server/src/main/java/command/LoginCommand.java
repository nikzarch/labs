package command;

import app.ConsoleApp;
import connection.Server;
import managers.DataBaseManager;
import org.apache.commons.codec.digest.DigestUtils;
import util.User;

public class LoginCommand extends Command{
    public LoginCommand() {
        super("login", "залогиниться");
    }

    @Override
    public void execute(String argument) {
        String[] loginPassword = argument.split("/");
        String login = loginPassword[0];
        String password = loginPassword[1];
        String hashedPassword = DigestUtils.md2Hex(password);
        DataBaseManager dataBaseManager = DataBaseManager.getInstance();
        if (dataBaseManager.isPasswordCorrect(login,hashedPassword) && dataBaseManager.userExists(login) ){
            ConsoleApp.commandPrint("Пароль верный, добро пожаловать");
            Server.setUser(new User(login,password));
        }else{
            ConsoleApp.fail("Пароль и/или логин неверный");
        }

    }
}
