package connection;

import dto.Request;
import dto.Response;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Objects;

public class Client {
    private DatagramChannel datagramChannel;
    private final InetSocketAddress address;
    private int port = 4086;


    public Client(InetAddress address, int port) {
        this.port = port;
        this.address = new InetSocketAddress(address, port);
    }

    public Client(InetAddress address) {
        this.address = new InetSocketAddress(address, port);

    }

    public void initialize() throws IOException {
        this.datagramChannel = DatagramChannel.open();
        datagramChannel.configureBlocking(false);
    }

    public void send(Request req) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(req);
        ByteBuffer toSend = ByteBuffer.wrap(byteArrayOutputStream.toByteArray());
        datagramChannel.send(toSend, address);
    }

    public Response receive() throws IOException, ClassNotFoundException {
        ByteBuffer toReceive = ByteBuffer.allocate(8192);
        SocketAddress socketAddress = datagramChannel.receive(toReceive);
        if (Objects.isNull(socketAddress)){
            throw new IOException("Ответа не получено");
        }
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(toReceive.array());
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        Response response = (Response) objectInputStream.readObject();
        return response;
    }

}
