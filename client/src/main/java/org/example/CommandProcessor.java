package org.example;

import org.example.connect.Sender;
import org.example.utility.HelperInputLab;
import org.example.models.LabWork;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystemException;
import java.util.List;

public class CommandProcessor {

    private static final List<String> COMMANDS_WITH_LABWORK =
        List.of("add", "add_if_max", "add_if_min", "update");

    private final HelperInputLab helper;
    private final Sender sender;
    private String login;
    private String password;
    BufferedReader br;
    Console console;
    Terminal terminal;
    LineReader reader;

    public CommandProcessor(HelperInputLab helper, Sender sender) throws IOException {
        br = new BufferedReader(new InputStreamReader(System.in));
        this.helper = helper;
        this.sender = sender;
        this.login = "";
        this.password = "";
        this.console = System.console();
        System.setProperty("org.jline.log.level", "OFF");
        this.terminal = TerminalBuilder.builder().build();
        this.reader = LineReaderBuilder.builder().terminal(terminal).build();
    }

    /**
     * Установить данные авторизации (вызывается после успешного login/register)
     */
    public void setCredentials(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public Request processCommand(CommandModel command) throws Exception {
        // Для auth команд всегда пропускаем
        if (command.getName().equals("register") || command.getName().equals("auth")) {
            return processAuthCommand(command);
        }

        // Для всех остальных команд - проверяем авторизацию
        if (!isAuthenticated()) {
            Request errorRequest = new Request("error");
            errorRequest.setData("Вы не авторизованы!!! Используйте 'auth <логин>' или зарегистрируйтесь");
            return errorRequest;
        }

        // Для execute_script
        if (command.getName().equals("execute_script")) {
            return processScript(command);
        }

        // Команды с LabWork
        if (requiresLabWork(command)) {
            return processWithLabWork(command);
        }

        // Обычные команды
        return processSimpleCommand(command);
    }

    private boolean requiresLabWork(CommandModel command) {
        return COMMANDS_WITH_LABWORK.contains(command.getName()) &&
                command.getArguments().length <= 1;
    }

    /**
     * Обработка команд авторизации
     */
    public boolean isAuthenticated() {
        return login != null && !login.isEmpty() && password != null && !password.isEmpty();
    }

    private Request processAuthCommand(CommandModel command) throws IOException {
        String[] args = command.getArguments();

        if (args.length < 1) {
            Request errorRequest = new Request("error");
            errorRequest.setData("Использование: " + command.getName() + " <логин>");
            return errorRequest;
        }

        Request request = new Request(command.getName());
        request.setLogin(args[0]);
        
        password = reader.readLine("Введите пароль: ", '*');
        request.setPassword(password);

        Request response = sender.sendAndReceive(request);

        // СОХРАНЯЕМ КРЕДЫ ПРИ УСПЕХЕ
        if (response != null && "success".equals(response.getCommandName())) {
            setCredentials(args[0], password);
            System.out.println("Успешный вход!!!");
        }

        return response;
    }

    /**
     * Команды с LabWork (add, update, add_if_min, add_if_max)
     */

    private Request processWithLabWork(CommandModel command) throws Exception {
        LabWork labWork = helper.inputLab();

        Request request = new Request(
            command.getName(),
            command.getArguments(),
            labWork,
            login,
            password
        );

        return sender.sendAndReceive(request);
    }

    /**
     * Выполнение скрипта
     */
    private Request processScript(CommandModel command) throws Exception {
        if (command.getArguments().length < 1) {
            throw new IllegalArgumentException("Укажите путь к файлу скрипта");
        }

        String scriptPath = command.getArguments()[0];
        String fileContent = readScriptFile(scriptPath);

        Request request = new Request(command.getName(), new String[]{fileContent}, login, password);
        request.setData(fileContent);

        return sender.sendAndReceive(request);
    }

    /**
     * Простые команды (show, info, remove_by_id, clear, help, history, exit)
     */
    private Request processSimpleCommand(CommandModel command) throws IOException {
        Request request = new Request(command.getName(), command.getArguments(), login, password);

        return sender.sendAndReceive(request);
    }

    private String readScriptFile(String scriptPath) throws IOException {
        File file = new File(scriptPath);

        if (!file.exists()) {
            throw new FileNotFoundException("Файл не найден: " + scriptPath);
        }
        if (!file.canRead()) {
            throw new FileSystemException("Нет прав на чтение: " + scriptPath);
        }
        if (!file.isFile()) {
            throw new FileNotFoundException("Указан не файл: " + scriptPath);
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            String content = new String(fis.readAllBytes(), StandardCharsets.UTF_8);
            if (content.trim().isEmpty()) {
                throw new IOException("Файл скрипта пуст");
            }
            return content;
        }
    }


}