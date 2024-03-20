package com.chatapp.conversation;

import com.chatapp.User;

import java.util.ArrayList;
import java.util.List;

public class Channel {
    private final String name;
    private final List<User> users;

    public Channel(String name) {
        this.name = name;
        this.users = new ArrayList<>();
    }

    public List<User> getUsers() {
        return users;
    }

    public String getName() {
        return name;
    }

}