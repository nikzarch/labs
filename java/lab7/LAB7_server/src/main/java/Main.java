import connection.Server;
import managers.CollectionManager;

public class Main {
    public static void main(String[] args) {
        try {
            CollectionManager.initialize();
        } catch (Exception exc) {
            System.out.println(exc.getMessage());
            exc.printStackTrace();
            System.out.println("Некорректные данные внутри БД или она пуста => коллекция пуста");
        }
        try {
            Server server = new Server();
            server.start();
        } catch (Exception exc) {
            System.out.println(exc.getMessage());
            exc.printStackTrace();
            System.out.println("Что-то пошло не так, попробуйте ещё раз");
        }

    }
}
