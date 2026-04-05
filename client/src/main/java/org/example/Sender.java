package org.example;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.Arrays;

import org.example.elem.LocalDateAdapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Sender implements AutoCloseable {
    private DatagramSocket socket;
    private InetAddress address;
    private int port;
    private byte[] receiveBuffer;
    private Gson gson;
    private static final int BUFFER_SIZE = 65536;
    private static final int TIMEOUT = 5000;

    public Sender(String host, int port) throws SocketException, UnknownHostException {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
                .create();
        this.socket = new DatagramSocket();
        this.address = InetAddress.getByName(host);
        this.port = port;
        this.receiveBuffer = new byte[BUFFER_SIZE];
        this.socket.setSoTimeout(TIMEOUT);
    }

    public void sendRequest(Request request) throws IOException {
        String jsonString = gson.toJson(request);
        byte[] data = jsonString.getBytes("UTF-8");
        DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
        socket.send(packet);
        System.out.println("Отправлено: " + request);
    }
    
    public void sendMessage(String message) throws IOException {
        Request request = new Request(message);
        sendRequest(request);
    }

    public Request receiveResponse() throws IOException {
        try {
            Arrays.fill(receiveBuffer, (byte) 0);
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(receivePacket);
            
            String response = new String(receivePacket.getData(), 0, receivePacket.getLength(), "UTF-8");
            System.out.println("Получен ответ: " + response);
            
            return gson.fromJson(response, Request.class);
            
        } catch (SocketTimeoutException e) {
            System.err.println("Таймаут ожидания ответа от сервера");
            Request timeoutRequest = new Request("timeout");
            timeoutRequest.setData("Время ожидания ответа истекло");
            return timeoutRequest;
        }
    }

    public Request sendAndReceive(Request request) throws IOException {
        sendRequest(request);
        return receiveResponse();
    }

    @Override
    public void close() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
            System.out.println("Сокет клиента закрыт");
        }
    }
}