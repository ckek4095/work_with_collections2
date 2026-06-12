package org.example.gui.localization;

import java.util.Locale;

public enum GuiLocale {
    RU("Русский", new Locale("ru", "RU")),
    BE("Беларуская", new Locale("be", "BY")),
    LV("Latviešu", new Locale("lv", "LV")),
    EN_IE("English (Ireland)", new Locale("en", "IE"));

    private final String displayName;
    private final Locale locale;

    GuiLocale(String displayName, Locale locale) {
        this.displayName = displayName;
        this.locale = locale;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Locale getLocale() {
        return locale;
    }

    @Override
    public String toString() {
        return displayName;
    }
}