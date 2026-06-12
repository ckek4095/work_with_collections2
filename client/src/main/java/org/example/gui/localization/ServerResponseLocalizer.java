package org.example.gui.localization;

import org.example.Request;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerResponseLocalizer {

    private static final Pattern ID_PATTERN = Pattern.compile("(?:id|ID)\\s+(\\d+)");
    private static final Pattern COUNT_PATTERN = Pattern.compile("(\\d+)\\s*(?:шт|элементов|элемента|element|pcs|gab)");
    private static final Pattern QUOTED_PATTERN = Pattern.compile("['\"]([^'\"]+)['\"]");

    private static final Map<String, String> EXACT_MATCH_MAP = new HashMap<>();
    private static final Map<Pattern, String> PATTERN_MAP = new HashMap<>();

    static {
        // === Точные соответствия для сообщений от ваших команд ===
        EXACT_MATCH_MAP.put(">>> Элемент успешно добавлен!", "server.object.added.success");
        EXACT_MATCH_MAP.put(">>> Элемент успешно добавлен!!!", "server.object.added.success");
        EXACT_MATCH_MAP.put(">>> Упс, элемент не максимальный😭", "server.object.not.max");
        EXACT_MATCH_MAP.put(">>> Упс, элемент не минимальный😭", "server.object.not.min");
        EXACT_MATCH_MAP.put("Пусто(((", "server.empty.collection");

        // === Шаблоны для сообщений с параметрами ===
        // Add, AddIfMax, AddIfMin
        PATTERN_MAP.put(Pattern.compile(">>> Элемент успешно добавлен!?"), "server.object.added.success");

        // RemoveById
        PATTERN_MAP.put(Pattern.compile(">>> Элемент с id (\\d+) удален"), "server.object.removed.by.id");
        PATTERN_MAP.put(Pattern.compile(">>> Элемент с id (\\d+) не найден или у вас нет прав"), "server.object.not.found.or.access");

        // UpdateById
        PATTERN_MAP.put(Pattern.compile(">>> Элемент с id (\\d+) успешно обновлен!"), "server.object.updated.by.id");
        PATTERN_MAP.put(Pattern.compile(">>> Ошибка при обновлении элемента"), "server.generic.error");

        // Clear
        PATTERN_MAP.put(Pattern.compile("Все ваши элементы удалены - (\\d+) шт"), "server.collection.cleared.count");

        // RemoveAllBy
        PATTERN_MAP.put(Pattern.compile(">>> Удалено элементов: (\\d+)"), "server.objects.removed.count");

        // FilterByDiscipline
        PATTERN_MAP.put(Pattern.compile(">>> Элементы с дисциплиной '(.*?)' не найдены"), "server.filter.discipline.not.found");

        // FilterStartsWith
        PATTERN_MAP.put(Pattern.compile(">>> Элементы, начинающиеся с '(.*?)', не найдены"), "server.filter.prefix.not.found");

        // Ошибки
        PATTERN_MAP.put(Pattern.compile("Ошибка сохранения в БД"), "server.db.save.error");
        PATTERN_MAP.put(Pattern.compile("Ошибка БД:.*"), "server.database.error");
        PATTERN_MAP.put(Pattern.compile("Ошибка: неправильный формат ID"), "server.error.invalid.id");
        PATTERN_MAP.put(Pattern.compile("Ошибка: неправильный формат числа"), "server.error.invalid.number");
        PATTERN_MAP.put(Pattern.compile("Ошибка: не указано название дисциплины"), "server.error.no.discipline");
        PATTERN_MAP.put(Pattern.compile("Ошибка: не указан префикс"), "server.error.no.prefix");
        PATTERN_MAP.put(Pattern.compile("Ошибка: ввод не может быть пустым"), "server.error.empty.input");
        PATTERN_MAP.put(Pattern.compile("Ошибка ввода-вывода"), "server.error.io");
        PATTERN_MAP.put(Pattern.compile("У вас нет прав на изменение этого элемента"), "server.access.denied");
        PATTERN_MAP.put(Pattern.compile("Элемент с ID .* не найден"), "server.object.not.found");
        PATTERN_MAP.put(Pattern.compile("Время ожидания ответа истекло"), "server.timeout.error");
        PATTERN_MAP.put(Pattern.compile("Время ожидания ответа истекло\\."), "server.timeout.error");
        PATTERN_MAP.put(Pattern.compile("timeout"), "server.timeout.error");
        PATTERN_MAP.put(Pattern.compile("Таймаут"), "server.timeout.error");

        // Show - данные коллекции (не локализуем, это данные)
        PATTERN_MAP.put(Pattern.compile("^-+$"), null); // игнорируем разделители

        // Общие шаблоны
        PATTERN_MAP.put(Pattern.compile("(?i).*(success|успех|паспях|veiksm).*"), "server.generic.success");
        PATTERN_MAP.put(Pattern.compile("(?i).*(error|ошибк|памылк|kļūda).*"), "server.generic.error");
        PATTERN_MAP.put(Pattern.compile("(?i).*(timeout|таймаут|чаc|gaidīšanas).*"), "server.timeout.error");
        PATTERN_MAP.put(Pattern.compile("(?i).*connection.*"), "server.connection.error");
    }

    public static String localize(Request response) {
        if (response == null) {
            return LocaleManager.get("server.no.response");
        }

        String responseText = response.getData() == null ? "" : response.getData().toString();
        return localize(responseText);
    }

    public static String localize(String serverMessage) {
        if (serverMessage == null || serverMessage.isEmpty()) {
            return LocaleManager.get("server.empty.response");
        }

        // Проверка точного совпадения
        if (EXACT_MATCH_MAP.containsKey(serverMessage)) {
            return LocaleManager.get(EXACT_MATCH_MAP.get(serverMessage));
        }

        // Проверка по шаблонам
        for (Map.Entry<Pattern, String> entry : PATTERN_MAP.entrySet()) {
            if (entry.getValue() == null) continue;

            Matcher matcher = entry.getKey().matcher(serverMessage);
            if (matcher.matches()) {
                String localizedKey = entry.getValue();
                String localized = LocaleManager.get(localizedKey);

                // Извлекаем параметры из сообщения
                if (matcher.groupCount() >= 1) {
                    String param = matcher.group(1);
                    if (localized.contains("%s")) {
                        return String.format(localized, param);
                    }
                }
                return localized;
            }
        }

        // Если сообщение содержит данные коллекции (с разделителями) - возвращаем как есть
        if (serverMessage.contains("----------------------------------------------------------") ||
                serverMessage.contains("ID:") && serverMessage.contains("Имя:")) {
            return serverMessage;
        }

        return serverMessage;
    }

    public static String localizeWithParams(String serverMessage, Object... params) {
        String localized = localize(serverMessage);
        if (params.length > 0 && (localized.contains("%s") || localized.contains("%d"))) {
            return String.format(localized, params);
        }
        return localized;
    }

    public static String localizeRemoveById(boolean success, Long id, String serverMessage) {
        if (success && id != null) {
            return String.format(LocaleManager.get("server.object.removed.by.id"), id);
        }
        return localize(serverMessage);
    }

    public static String localizeUpdateById(boolean success, Long id, String serverMessage) {
        if (success && id != null) {
            return String.format(LocaleManager.get("server.object.updated.by.id"), id);
        }
        return localize(serverMessage);
    }

    public static String localizeFilterResponse(String serverMessage, String filterValue) {
        if (serverMessage == null || serverMessage.isEmpty()) {
            return String.format(LocaleManager.get("server.filter.no.results"), filterValue);
        }

        // Проверяем, есть ли в сообщении количество найденных элементов
        Pattern countPattern = Pattern.compile("(\\d+)");
        Matcher matcher = countPattern.matcher(serverMessage);

        if (serverMessage.contains("не найдены") || serverMessage.contains("не найдена")) {
            return String.format(LocaleManager.get("server.filter.no.results"), filterValue);
        }

        if (matcher.find()) {
            try {
                long count = Long.parseLong(matcher.group());
                return String.format(LocaleManager.get("server.filter.results"), count, filterValue);
            } catch (NumberFormatException e) {
                return String.format(LocaleManager.get("server.filter.no.results"), filterValue);
            }
        }

        return localize(serverMessage);
    }
}