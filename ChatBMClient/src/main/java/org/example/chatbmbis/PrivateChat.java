package org.example.chatbmbis;

import java.util.ArrayList;
import java.util.List;

public class PrivateChat {
    private List<Message> messages;
    private User user1;
    private User user2;
    private String name;


    public PrivateChat(User user1, User user2) {
        this.user1 = user1;
        this.user2 = user2;
        this.messages = new ArrayList<>();
        this.name = user1.getName() + "|" + user2.getName();
    }

    public String getName() {
        return name;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }
}
