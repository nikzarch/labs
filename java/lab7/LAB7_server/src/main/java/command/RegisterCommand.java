package command;

import app.ConsoleApp;
import connection.Server;
import managers.DataBaseManager;
import org.apache.commons.codec.digest.DigestUtils;
import util.User;

import java.sql.SQLException;

public class RegisterCommand extends Command {

    public RegisterCommand() {
        super("регистрация", "чтоб регаться");
    }

    @Override
    public void execute(String argument) {
        String[] loginPassword = argument.split("/");
        String login = loginPassword[0];
        String password = loginPassword[1];
        String hashedPassword = DigestUtils.md2Hex(password);
        DataBaseManager dataBaseManager = DataBaseManager.getInstance();
        if (!dataBaseManager.userExists(login)) {
            try {
                DataBaseManager.getInstance().addUser(login, hashedPassword);
                ConsoleApp.commandPrint("Вы зарегистрированы");
                Server.setUser(new User(login,password));
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                ConsoleApp.fail("Не удалось зарегистрироваться, проблема в БД");
            }
        }else{
            ConsoleApp.fail("Такой логин уже существует");
        }
    }
}
