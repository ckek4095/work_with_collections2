package org.example.gui.localization;

public class LabWorkResponseLocalizer {

    public static String localizeFilterResponse(String serverMessage, String filterValue) {
        if (serverMessage == null || serverMessage.isEmpty()) {
            return String.format(LocaleManager.get("server.filter.no.results"), filterValue);
        }

        // Пытаемся извлечь количество из сообщения
        long count = extractCount(serverMessage);

        if (count == 0) {
            return String.format(LocaleManager.get("server.filter.no.results"), filterValue);
        }

        return String.format(LocaleManager.get("server.filter.results"), count, filterValue);
    }

    private static long extractCount(String message) {
        // Пытаемся найти число в сообщении
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\d+");
        java.util.regex.Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            try {
                return Long.parseLong(matcher.group());
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }
}