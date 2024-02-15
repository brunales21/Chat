package org.example.chatbmbis;

public class ChatRepeatedException extends ChatException {
    public ChatRepeatedException(String name) {
        super(name + " ya existe.");
    }
}
