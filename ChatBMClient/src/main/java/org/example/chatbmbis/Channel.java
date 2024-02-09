package org.example.chatbmbis;

import java.util.ArrayList;
import java.util.List;

public class Channel {
    private final String name;
    private final List<Message> messages;
    private final List<User> users;

    public Channel(String name) {
        this.name = name;
        this.users = new ArrayList<>();
        this.messages = new ArrayList<>();
    }

    public void addUser(User user) {
        users.add(user);
    }

    public List<User> getUsers() {
        return users;
    }

    public String getName() {
        return name;
    }

}