package connection;

import app.ConsoleApp;
import command.Command;
import dto.ExecutionStatus;
import dto.Request;
import dto.Response;
import managers.CollectionManager;
import util.User;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Objects;
import java.util.concurrent.ForkJoinPool;

public class Server {
    private static final String host = "localhost";
    private static int port = 4086;
    private DatagramPacket datagramPacket;
    private final DatagramSocket datagramSocket;
    private byte[] buf = new byte[8192];
    private final ForkJoinPool forkJoinPool = new ForkJoinPool();
    private static User user = new User("unauthorized","");

    public Server(int port) throws IOException {
        Server.port = port;
        datagramSocket = new DatagramSocket(port);
    }

    public Server() throws IOException {
        datagramSocket = new DatagramSocket(port);
    }

    public void start() throws IOException{
        System.out.println("Сервер запущен");
        ConsoleApp.clearResponseString();
        while (true) {
            buf = new byte[8192];
            datagramPacket = new DatagramPacket(buf, buf.length);
            datagramSocket.receive(datagramPacket);

            forkJoinPool.submit(() -> {
                try {
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(datagramPacket.getData());
                    ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                    Request req = (Request) objectInputStream.readObject();
                    handleRequest(req, datagramPacket);
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }).join();
        }
    }

    private void handleRequest(Request req, DatagramPacket datagramPacket) {
        String commandName = req.getCommandName();
        if (commandName.contains("add")) {
            Command.commandList.get("add").execute(req.getObjectArgument());
        } else if (commandName.equals("update")) {
            if (Objects.isNull(req.getObjectArgument())) {
                CollectionManager.ticketExistsAndCanBeUpdated(Integer.parseInt(req.getCommandArgument()), req.getUser().getName());
            } else {
                req.getObjectArgument().setId(Long.parseLong(req.getCommandArgument()));
                CollectionManager.updateTicket(Long.parseLong(req.getCommandArgument()), req.getObjectArgument());
            }
        } else if (commandName.equals("remove_by_id")) {
            Command.commandList.get("remove_by_id").execute(req.getCommandArgument(), req.getUser().getName());
        } else {
            Command.commandList.get(req.getCommandName()).execute(req.getCommandArgument());
        }
        System.out.println("Выполняется команда " + commandName + " пользователем " + user.getName());
        String responseString = ConsoleApp.getResponseString();
        Response response;
        if (responseString.contains("Команда не была выполнена корректно по причине: ")) {
            response = new Response(responseString, ExecutionStatus.FAILURE);
        } else {
            response = new Response(responseString, ExecutionStatus.SUCCESS);
        }
        sendResponse(response, datagramPacket);
    }

    private void sendResponse(Response response, DatagramPacket datagramPacket) {
        new Thread(() -> {
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                objectOutputStream.writeObject(response);
                byte[] responseBuf = byteArrayOutputStream.toByteArray();
                InetAddress senderAddress = datagramPacket.getAddress();
                int senderPort = datagramPacket.getPort();
                DatagramPacket responsePacket = new DatagramPacket(responseBuf, responseBuf.length, senderAddress, senderPort);
                datagramSocket.send(responsePacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start(); //
    }

    public static User getUser() {
        return user;
    }

    public static void setUser(User user1) {
        user = user1;
    }
}
