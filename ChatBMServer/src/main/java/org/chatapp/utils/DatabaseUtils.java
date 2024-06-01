package org.chatapp.utils;

public class DatabaseUtils {

    public static final String DB_NAME = "chat.db";
    public static final String USER_TABLE_NAME = "user";

    public static String createUserTable(String name) {
        return "CREATE TABLE IF NOT EXISTS " + name + " (nickname TEXT PRIMARY KEY, password TEXT NOT NULL);";
    }
}
