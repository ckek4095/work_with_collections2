package org.example.gui.localization;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocaleManager {
    private static GuiLocale currentLocale = GuiLocale.RU;
    private static final List<Runnable> listeners = new ArrayList<>();

    private LocaleManager() {
    }

    public static GuiLocale getCurrentGuiLocale() {
        return currentLocale;
    }

    public static Locale getCurrentLocale() {
        return currentLocale.getLocale();
    }

    public static void setCurrentLocale(GuiLocale locale) {
        if (locale == null) {
            return;
        }

        currentLocale = locale;
        notifyListeners();
    }

    public static String get(String key) {
        return GuiResources.get(currentLocale, key);
    }

    public static NumberFormat getNumberFormat() {
        return NumberFormat.getNumberInstance(getCurrentLocale());
    }

    public static DateFormat getDateTimeFormat() {
        return DateFormat.getDateTimeInstance(
                DateFormat.MEDIUM,
                DateFormat.SHORT,
                getCurrentLocale()
        );
    }

    public static void addLocaleChangeListener(Runnable listener) {
        listeners.add(listener);
    }

    private static void notifyListeners() {
        for (Runnable listener : listeners) {
            listener.run();
        }
    }
}