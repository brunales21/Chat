package com.chatapp.model;

import java.util.ArrayList;
import java.util.List;

public class PrivateChat {

    private final List<Message> messages;
    private final User user;

    public PrivateChat(User user) {
        this.user = user;
        this.messages = new ArrayList<>();
    }

    public User getUser() {
        return user;
    }

    public List<Message> getMessages() {
        return messages;
    }
}
