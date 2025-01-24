package app;

/**
 * Класс для инициализации и работы консольного приложения
 */
public class ConsoleApp {
    private static StringBuilder responseString = new StringBuilder();

    /**
     * Метод для формирования строки ответа
     *
     * @param string
     */
    public static void commandPrint(String string) {
        responseString.append(string + "\n");
    }

    public static void fail(String reason) {
        responseString = new StringBuilder("Команда не была выполнена корректно по причине: " + reason);
    }

    public static String getResponseString() {
        String toReturn = responseString.toString();
        responseString = new StringBuilder();
        return toReturn;
    }

    public static void clearResponseString(){
        responseString = new StringBuilder();
    }


}
