package org.example;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

public class Sender implements AutoCloseable {
    private DatagramSocket socket;
    private InetAddress address;
    private int port;
    private byte[] receiveBuffer;
    private static final int BUFFER_SIZE = 65536;
    private static final int TIMEOUT = 5000;

    public Sender(String host, int port) throws SocketException, UnknownHostException {
        this.socket = new DatagramSocket();
        this.address = InetAddress.getByName(host);
        this.port = port;
        this.receiveBuffer = new byte[BUFFER_SIZE];
        this.socket.setSoTimeout(TIMEOUT);
    }

    public void sendMessage(String message) throws IOException {
        byte[] data = message.getBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
        socket.send(packet);
        System.out.println("Отправлено: " + message);
    }

    public String receiveResponse() throws IOException {
        Arrays.fill(receiveBuffer, (byte) 0);
        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
        socket.receive(receivePacket);
        return new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();
    }
    
    public String sendAndReceive(String message) throws IOException {
        sendMessage(message);
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