package org.example;

import java.io.IOException;

public class MainServer {
    public static void main(String[] args) throws IOException {
        ServerInitializer initializer = new ServerInitializer();
        ServerRuntime runtime = initializer.initialize();
        runtime.start();
    }
}