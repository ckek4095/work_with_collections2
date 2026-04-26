package org.example;

public class ResponseHandler {

    public void handleResponse(Request response) {
        if (response == null) return;

        String commandName = response.getCommandName();
        Object data = response.getData();
        String dataStr = data != null ? data.toString() : ">>>";

        switch (commandName) {
            case "success":
                printSuccess(dataStr);
                break;
            case "error":
                printError(dataStr);
                break;
            case "exit":
                printExit(dataStr);
                break;
            case "timeout":
                printTimeout(dataStr);
                break;
            default:
                printDefault(dataStr);
        }
    }

    private void printSuccess(String data) {
        System.out.println("✓ " + data);
    }

    private void printError(String data) {
        System.err.println("Ошибка: " + data);
    }

    private void printExit(String data) {
        System.out.println("Сервер завершает работу: " + data);
    }

    private void printTimeout(String data) {
//        System.err.println("Таймаут: " + data);
    }

    private void printDefault(String data) {
        System.out.println("Ответ: " + data);
    }

    // Проверка успешности ответа
    public boolean isSuccess(Request response) {
        return response != null && "success".equals(response.getCommandName());
    }

    // Проверка ошибки
    public boolean isError(Request response) {
        return response != null && "error".equals(response.getCommandName());
    }
}