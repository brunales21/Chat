package org.example.chatbmbis;

import java.util.ArrayList;
import java.util.List;

public class Channel {
    private final String name;
    private final List<String> users;

    public Channel(String name) {
        this.name = name;
        this.users = new ArrayList<>();
    }

    public void addUser(String user) {
        users.add(user);
    }

    public List<String> getUsers() {
        return users;
    }

    public String getName() {
        return name;
    }

}