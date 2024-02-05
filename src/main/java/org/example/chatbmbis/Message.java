package org.example.chatbmbis;

public class Message {
    private User sender;
    private String textMessage;

    public Message(User sender, String textMessage) {
        this.sender = sender;
        this.textMessage = textMessage;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }
}
