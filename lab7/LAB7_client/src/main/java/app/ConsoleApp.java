package app;

import collection.EventType;
import collection.IncorrectDataTypeException;
import collection.Ticket;
import collection.TicketType;
import command.ExecutionFailedCommandException;
import command.UnexistingCommandException;
import connection.Client;
import dto.ExecutionStatus;
import dto.Request;
import dto.Response;
import managers.FileManager;
import util.User;

import java.io.*;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Objects;

import static command.Command.commandList;

/**
 * Класс для инициализации и работы консольного приложения
 */
public class ConsoleApp {
    public static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private User user;
    private static ConsoleApp consoleApp;
    private Client udpClient;

    private ConsoleApp() {
        while (true) {
            try {
                udpClient = new Client(InetAddress.getLocalHost(), 4086);
                udpClient.initialize();
                break;
            } catch (IOException e) {
                System.out.println(e.getMessage());
                System.out.println("Сервер недоступен, еще одна попытка");
            }
        }
    }


    public void start() {
        logUser();
        System.out.println("Введите help для инструкции");
        while (true) {
            try {
                System.out.print(">");
                String input = reader.readLine();
                if (Objects.isNull(input)) {
                    System.exit(0);
                }
                String[] commandString = input.strip().split(" ");
                try {
                    String argument = commandString.length > 1 ? commandString[1] : "";
                    Request request;
                    if (isValid(commandString[0], argument)) {
                        if (commandString[0].equals("execute_script")) {
                            ArrayList<String> lines = FileManager.readLinesFromFile(new BufferedReader(new InputStreamReader(new FileInputStream(argument))));
                            for (String i : lines) {
                                String[] line = i.strip().split(" ");
                                argument = line.length > 1 ? line[1] : "";
                                request = new Request(line[0], argument, user);
                                Response response = sendAndReceive(request);
                                System.out.println(response.getMessage());
                            }
                        } else if (commandString[0].equals("logout")){
                            request = commandList.get(commandString[0]).execute(argument, user);
                            Response response = sendAndReceive(request);
                            System.out.println(response.getMessage());
                            this.user = null;
                            logUser();
                        } else {
                            if (commandString[0].equals("exit")) {
                                System.exit(0);
                            }
                            request = commandList.get(commandString[0]).execute(argument, user);
                            Response response = sendAndReceive(request);
                            System.out.println(response.getMessage());

                        }
                    }else {
                        throw new UnexistingCommandException("");
                    }
                } catch (ExecutionFailedCommandException e) {
                } catch (FileNotFoundException e) {
                    System.out.println("Проблема с доступом к файлу");
                } catch (IOException e) {
                } catch (UnexistingCommandException exc) {
                    System.out.println("Такой команды нет"); // Cannot invoke "lab5.command.Command.execute(String)" because the return value of "java.util.HashMap.get(Object)" is null
                }
            } catch (Exception exc) {
                System.out.println(exc.getMessage());
                System.out.println("Что-то пошло не так...");
                System.exit(0);
            }
        }

    }

    /**
     * Метод печати в консоль
     *
     * @param string
     */
    public static void commandPrint(String string) {
        System.out.println(string);
    }

    public boolean checkIdForUpdate(Request request) {
        Response response = sendAndReceive(request); // тут без юзера, потому что первый запрос на проверку айди
        if (response.getMessage().contains("Айди не существует")) {
            System.out.println(response.getMessage());
            return false;
        } else {
            return true;
        }
    }

    public static Ticket formTicketToSend(User user) {
        Ticket ticket = new  Ticket();
        ConsoleApp.commandPrint("Введите название билета");
        ticket.setName(askName(ConsoleApp.reader));

        ConsoleApp.commandPrint("Введите координату X (вещественное число)");
        ticket.setCoordinatesX(askCoordinatesX(ConsoleApp.reader));

        ConsoleApp.commandPrint("Введите координату Y (целое число)");
        ticket.setCoordinatesY(askCoordinatesY(ConsoleApp.reader));

        ConsoleApp.commandPrint("Введите цену");
        ticket.setPrice(askPrice(ConsoleApp.reader));

        ConsoleApp.commandPrint("Введите скидку (от 1 до 100%)");
        ticket.setDiscount(askDiscount(ConsoleApp.reader));

        ConsoleApp.commandPrint("Введите тип билета");
        ConsoleApp.commandPrint("Типы билетов: VIP(\"ВИП\"),USUAL(\"Обычный\"),BUDGETARY(\"Бюджетный\"), CHEAP(\"Дешёвый\")");
        ticket.setType(askType(ConsoleApp.reader));

        ConsoleApp.commandPrint("Введите название мероприятия");
        ticket.getEvent().setName(askEventName(ConsoleApp.reader));

        ConsoleApp.commandPrint("Введите тип мероприятия");
        ConsoleApp.commandPrint("Типы мероприятий: CONCERT(\"Концерт\"), BASKETBALL(\"Баскетбол\"), THEATRE_PERFORMANCE(\"Театральное представление\")");
        ticket.getEvent().setEventType(askEventType(ConsoleApp.reader));

        ConsoleApp.commandPrint("Введите количество билетов (целое число)");
        ticket.getEvent().setTicketsCount(askEventTicketsCount(ConsoleApp.reader));
        ticket.setUserName(user.getName());
        return ticket;
    }

    private static boolean isValid(String commandName, String argument) {
        if (commandList.containsKey(commandName)) {
            switch (commandName) {
                case "execute_script", "filter_starts_with_name":
                    return !argument.isBlank();

                case "filter_by_discount":
                    try {
                        Float.parseFloat(argument);
                        return true;
                    } catch (NumberFormatException exc) {
                        return false;
                    }
                case "update":
                    try {
                        Integer.parseInt(argument);
                        return true;
                    } catch (NumberFormatException exc) {
                        return false;
                    }
                default:
                    return true;
            }
        } else {
            return false;
        }
    }

    public static String askName(BufferedReader reader) {
        while (true) {
            try {
                String input = reader.readLine().strip();
                if (input.isBlank()) {
                    throw new IncorrectDataTypeException("Имя не может быть пустым");
                } else {
                    return input;
                }
            } catch (IOException exc) {
                System.out.println("Неверный формат ввода, попробуйте еще раз");
            } catch (IncorrectDataTypeException exc) {
                System.out.println(exc.getMessage());
            }

        }
    }

    public static String askCoordinatesX(BufferedReader reader) {
        while (true) {
            try {
                String input = reader.readLine().strip();
                Double x = Double.parseDouble(input);
                if (input.isBlank()) {
                    throw new IncorrectDataTypeException("Координата X не может быть пустой");
                } else {
                    return input;
                }
            } catch (IOException | NumberFormatException | NullPointerException exc) {
                System.out.println("Неверный формат ввода, попробуйте еще раз");
            } catch (IncorrectDataTypeException exc) {
                System.out.println(exc.getMessage());
            }
        }
    }

    public static String askCoordinatesY(BufferedReader reader) {
        while (true) {
            try {
                String input = reader.readLine().strip();
                Integer y = Integer.parseInt(input);
                if (input.isBlank()) {
                    throw new IncorrectDataTypeException("Координата Y не может быть пустой");
                } else {
                    return input;
                }
            } catch (IOException | NumberFormatException | NullPointerException exc) {
                System.out.println("Неверный формат ввода, попробуйте еще раз");
            } catch (IncorrectDataTypeException exc) {
                System.out.println(exc.getMessage());
            }

        }
    }

    public static String askPrice(BufferedReader reader) {
        while (true) {
            try {
                String input = reader.readLine().strip();
                Double price = Double.parseDouble(input.replace(',','.'));
                if (input.isBlank()) {
                    throw new IncorrectDataTypeException("Цена не может быть пустой");
                } else if (price < 0) {
                    throw new IncorrectDataTypeException("Цена не может быть отрицательной");
                } else {
                    return input;
                }
            } catch (IOException  | NullPointerException  | NumberFormatException exc) {
                System.out.println("Неверный формат ввода, попробуйте еще раз");
            } catch (IncorrectDataTypeException exc) {
                System.out.println(exc.getMessage());
            }

        }
    }

    public static String askDiscount(BufferedReader reader) {
        while (true) {
            try {
                String input = reader.readLine().strip();
                Float discount = Float.parseFloat(input);
                if (input.isBlank()) {
                    throw new IncorrectDataTypeException("Скидка не может быть пустой");
                } else if (discount < 0 || discount > 100) {
                    throw new IncorrectDataTypeException("Скидка не может быть меньше нуля или больше ста процентов");
                } else {
                    return input;
                }
            } catch (IOException | NumberFormatException | NullPointerException exc) {
                System.out.println("Неверный формат ввода, попробуйте еще раз");
            } catch (IncorrectDataTypeException exc) {
                System.out.println(exc.getMessage());
            }

        }
    }

    public static TicketType askType(BufferedReader reader) {
        while (true) {
            try {
                String input = reader.readLine().toUpperCase().strip();
                if (input.isBlank()) {
                    throw new IncorrectDataTypeException("Тип билета не может быть пустым");
                } else {
                    try {
                        Integer intTicketType = Integer.parseInt(input); // приводим к инту, если не приводится ловим ошибку и задаём строкой
                        return TicketType.values()[intTicketType - 1];
                    } catch (NumberFormatException exc) {
                        try {
                            return TicketType.valueOf(input);
                        } catch (IllegalArgumentException e) {
                            throw new IncorrectDataTypeException("Нераспознанный тип билета");
                        }
                    }
                }
            } catch (IOException | IllegalArgumentException | IncorrectDataTypeException |
                     IndexOutOfBoundsException | NullPointerException exc) {
                System.out.println("Неверный формат ввода, попробуйте еще раз");
            }

        }
    }

    public static String askEventName(BufferedReader reader) {
        while (true) {
            try {
                String input = reader.readLine().strip();
                if (input.isBlank()) {
                    throw new IncorrectDataTypeException("Имя мероприятия не может быть пустым");
                } else {
                    return input;
                }
            } catch (IOException | NullPointerException exc) {
                System.out.println("Неверный формат ввода, попробуйте еще раз");
            } catch (IncorrectDataTypeException exc) {
                System.out.println(exc.getMessage());
            }

        }
    }

    public static String askEventTicketsCount(BufferedReader reader) {
        while (true) {
            try {
                String input = reader.readLine().strip();
                Long ticketsCount = Long.parseLong(input);
                if (input.isBlank()) {
                    throw new IncorrectDataTypeException("Количество билетов не может быть пустым");
                } else if (ticketsCount < 0) {
                    throw new IncorrectDataTypeException("Количество билетов не может быть отрицательным");
                } else {
                    return input;
                }
            } catch (IOException | NumberFormatException  | NullPointerException exc) {
                System.out.println("Неверный формат ввода, попробуйте еще раз");
            } catch (IncorrectDataTypeException exc) {
                System.out.println(exc.getMessage());
            }

        }
    }

    public static EventType askEventType(BufferedReader reader) {
        while (true) {
            try {
                String input = reader.readLine().toUpperCase().strip();
                if (input.isBlank()) {
                    throw new IncorrectDataTypeException("Тип мероприятия не может быть пустым");
                } else {
                    try {
                        Integer intTicketType = Integer.parseInt(input); // приводим к инту, если не приводится ловим ошибку и задаём строкой
                        return EventType.values()[intTicketType - 1];
                    } catch (NumberFormatException exc) {
                        return EventType.valueOf(input);
                    }
                }
            } catch (IOException | IllegalArgumentException | IndexOutOfBoundsException  | NullPointerException exc) {
                System.out.println("Неверный формат ввода, попробуйте еще раз");
            } catch (IncorrectDataTypeException exc) {
                System.out.println(exc.getMessage());
            }
        }
    }

    /**
     * Запуск приложения
     */


    public void logUser() {
        while (true) {
            try {
                System.out.println("Для регистрации введите команду register, для авторизации login");
                String input = reader.readLine();
                if (input.equals("register")) {
                    String[] loginPassword = askLoginAndPassword();
                    String login = loginPassword[0];
                    String password = loginPassword[1];
                    Request request = new Request("register", login + "/" + password);
                    Response response = sendAndReceive(request);
                    if (response.getStatus().equals(ExecutionStatus.FAILURE)) {
                        System.out.println("Не удалось зарегистрироваться, попробуйте ещё раз");
                    } else {
                        System.out.println(response.getMessage());
                        this.user = new User(login, password);
                        break;
                    }

                } else if (input.equals("login")) {
                    String[] loginPassword = askLoginAndPassword();
                    String login = loginPassword[0];
                    String password = loginPassword[1];
                    Request request = new Request("login", login + "/" + password);
                    Response response = sendAndReceive(request);
                    if (response.getStatus().equals(ExecutionStatus.FAILURE)) {
                        System.out.println(response.getMessage());
                        System.out.println("Не удалось авторизоваться, попробуйте ещё раз");
                    } else {
                        System.out.println(response.getMessage());
                        this.user = new User(login, password);
                        break;
                    }
                }
            } catch (IOException exc) {
                System.out.println("проблемы с авторизацией, выходим....");
                System.exit(0);
            }
        }
    }

    private String[] askLoginAndPassword() throws IOException {
        String login, password;
        while (true) {
            System.out.println("Введите логин");
            System.out.println("Разрешены буквы латинского алфавита, цифры, подчеркивания и тире, длина от 3 до 500 символов");
            login = reader.readLine();
            if (login.matches("^[a-zA-Z0-9_-]{3,}$")) {
                break;
            }
            System.out.println("Присутствуют лишние символы/длина не соответствует, попробуйте еще раз");
        }
        while (true) {
            System.out.println("Введите пароль");
            System.out.println("Разрешены буквы латинского алфавита, цифры, подчеркивания и тире, длина от 3 до 500 символов");
            password = reader.readLine();
            if (password.matches("^[a-zA-Z0-9_-]{3,}$")) {
                break;
            }
            System.out.println("Присутствуют лишние символы, попробуйте еще раз");
        }
        return new String[]{login, password};
    }

    private Response sendAndReceive(Request req)  {
        for (int i = 0; i < 30; i++) {
            try {
                udpClient.send(req);
                Thread.sleep(2000);
                Response response = udpClient.receive();
                return response;
            } catch (Exception exc) {
                System.out.println("Сервер недоступен, пробуем еще раз");
            }
        }
        return new Response("Не получилось, попробуйте потом", ExecutionStatus.FAILURE);
    }

    public static ConsoleApp getInstance() {
        if (Objects.isNull(consoleApp)) {
            consoleApp = new ConsoleApp();
        }
        return consoleApp;
    }
}
