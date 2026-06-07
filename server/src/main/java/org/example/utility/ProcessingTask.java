package org.example.utility;

import org.example.Request;
import org.example.managers.Runner;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.RecursiveAction;

public class ProcessingTask extends RecursiveAction {
    private final Request request;
    private final InetAddress clientAddress;
    private final int clientPort;
    private final Runner runner; // Ссылка на Runner для доступа к методам

    public ProcessingTask(Request request, InetAddress clientAddress, int clientPort, Runner runner) {
        this.request = request;
        this.clientAddress = clientAddress;
        this.clientPort = clientPort;
        this.runner = runner;
    }

    @Override
    protected void compute() {
        // Обрабатываем команду (вызываем метод Runner)
        Request response = runner.processRequest(request);

        // Отправка ответа в НОВОМ ПОТОКЕ (строго по ТЗ)
        try {
            runner.sendResponse(clientAddress, clientPort, response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("[" + Thread.currentThread().getName() + "] ForkJoinPool обработал команду: " +
                request.getCommandName());
    }
}