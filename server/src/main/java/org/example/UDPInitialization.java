package org.example;

import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPInitialization {
    private static final int PORT = 60000;
    private final DatagramSocket socket;

    public UDPInitialization() throws SocketException {
        this.socket = new DatagramSocket(PORT);
        System.out.println("UDP сокет создан на порту " + PORT);
    }

    public DatagramSocket getSocket() {
        return socket;
    }

    public int getPort() {
        return PORT;
    }
}