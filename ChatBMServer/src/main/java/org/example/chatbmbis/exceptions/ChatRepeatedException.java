package org.example.chatbmbis.exceptions;

public class ChatRepeatedException extends ChatException {
    public ChatRepeatedException(String name) {
        super(name + " ya existe.");
    }
}
