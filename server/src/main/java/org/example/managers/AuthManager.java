package org.example.managers;

import org.example.db.DatabaseManager;
import org.example.User;

public class AuthManager {
    private DatabaseManager dbManager;

    public AuthManager(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public User authenticate(String login, String password) {
        if (login == null || password == null) return null;
        return dbManager.authenticate(login, password);
    }

    public boolean register(String login, String password) {
        if (login == null || password == null || login.trim().isEmpty()) {
            return false;
        }
        return dbManager.registerUser(login, password);
    }
}