package org.example.connect;

import java.io.*;
import java.net.*;

import org.example.Request;
import org.example.Response;
import org.example.ResponseStatus;

public class Sender implements AutoCloseable {
    private DatagramSocket socket;
    private InetAddress address;
    private int port;
    private static final int BUFFER_SIZE = 65536;
    private static final int TIMEOUT = 5000;

    public Sender(String host, int port) throws SocketException, UnknownHostException {
        this.socket = new DatagramSocket();
        this.address = InetAddress.getByName(host);
        this.port = port;
        this.socket.setSoTimeout(TIMEOUT);
    }

    public void sendRequest(Request request) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(request);
        oos.flush();
        byte[] data = baos.toByteArray();
        DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
        socket.send(packet);
        System.out.println("Отправлено: " + request);
    }

    public Object receiveObject() throws IOException {
        try {
            byte[] receiveBuffer = new byte[BUFFER_SIZE];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(receivePacket);

            ByteArrayInputStream bais = new ByteArrayInputStream(receivePacket.getData(), 0, receivePacket.getLength());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();

        } catch (SocketTimeoutException e) {
            System.err.println("Таймаут ожидания ответа от сервера");
            return new Response(ResponseStatus.ERROR, "Время ожидания ответа истекло");
        } catch (ClassNotFoundException e) {
            System.err.println("Ошибка десериализации: " + e.getMessage());
            return new Response(ResponseStatus.ERROR, "Ошибка формата данных");
        }
    }

    public Response sendAndReceive(Request request) throws IOException {
        sendRequest(request);
        Object obj = receiveObject();
        if (obj instanceof Response) {
            return (Response) obj;
        } else if (obj instanceof Request) {
            Request oldResponse = (Request) obj;
            Response response = new Response(ResponseStatus.SUCCESS, (String) oldResponse.getData());
            response.setPayload(oldResponse.getData());
            return response;
        }
        return new Response(ResponseStatus.ERROR, "Неизвестный тип ответа");
    }

    @Override
    public void close() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
            System.out.println("Сокет клиента закрыт");
        }
    }
}