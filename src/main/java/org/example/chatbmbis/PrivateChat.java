package org.example.chatbmbis;

import java.util.List;

public class PrivateChat {
    private List<Message> messages;
    private User user1;
    private User user2;


    public PrivateChat(User user1, User user2) {
        this.user1 = user1;
        this.user2 = user2;
    }
}
