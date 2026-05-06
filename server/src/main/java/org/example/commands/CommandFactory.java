package org.example.commands;

import org.example.db.DatabaseManager;
import org.example.managers.CollectionManager;
import org.example.managers.HelperInputLabManager;
import org.example.models.LabWork;

import java.util.List;

interface CommandFactory {
    Command create(CollectionManager colManager,
                   HelperInputLabManager helperInput,
                   String[] args,
                   List<String> history,
                   LabWork labWork,
                   Integer ownerId,
                   DatabaseManager databaseManager);
}