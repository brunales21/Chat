package org.chatapp.utils;

public class DatabaseUtils {

    public static final String USER_TABLE_NAME = "user";

    public static String getTableCreationQuery(String name) {
        return "CREATE TABLE IF NOT EXISTS " + name + " (nickname TEXT PRIMARY KEY, contraseña TEXT NOT NULL);";
    }
}
