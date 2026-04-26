package org.example;

import org.example.connect.Sender;
import org.example.utility.HelperInputLab;
import org.example.models.LabWork;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystemException;
import java.util.List;

public class CommandProcessor {

    private static final List<String> COMMANDS_WITH_LABWORK = List.of("add", "add_if_max", "add_if_min", "update");

    private final HelperInputLab helper;
    private final Sender sender;
    private final String username;

    public CommandProcessor(HelperInputLab helper, Sender sender, String username) {
        this.helper = helper;
        this.sender = sender;
        this.username = username;
    }

    public Request processCommand(CommandModel command) throws Exception {
        if (requiresLabWork(command)) {
            return processWithLabWork(command);
        } else if (command.getName().equals("execute_script")) {
            return processScript(command);
        } else {
            return processSimpleCommand(command);
        }
    }

    private boolean requiresLabWork(CommandModel command) {
        return COMMANDS_WITH_LABWORK.contains(command.getName()) &&
                command.getArguments().length <= 1;
    }

    private Request processWithLabWork(CommandModel command) throws Exception {
        LabWork labWork = helper.inputLab();
        Request request = new Request(command.getName(), command.getArguments(), labWork);
        request.setUsername(username);
        return sender.sendAndReceive(request);
    }

    private Request processScript(CommandModel command) throws Exception {
        if (command.getArguments().length < 1) {
            throw new IllegalArgumentException("Укажите путь к файлу скрипта");
        }

        String scriptPath = command.getArguments()[0];
        String fileContent = readScriptFile(scriptPath);

        Request request = new Request(command.getName(), new String[]{fileContent});
        request.setUsername(username);
        request.setData(fileContent); // Сохраняем содержимое скрипта в data
        return sender.sendAndReceive(request);
    }

    private String readScriptFile(String scriptPath) throws IOException {
        File file = new File(scriptPath);

        if (!file.exists()) {
            throw new FileNotFoundException("Файл не найден: " + scriptPath);
        }
        if (!file.canRead()) {
            throw new FileSystemException("Недостаточно прав на чтение файла: " + scriptPath);
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

    private Request processSimpleCommand(CommandModel command) throws IOException {
        Request request = new Request(command.getName(), command.getArguments());
        request.setUsername(username);
        return sender.sendAndReceive(request);
    }
}