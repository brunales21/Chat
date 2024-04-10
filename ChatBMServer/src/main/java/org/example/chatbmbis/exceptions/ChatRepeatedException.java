package org.example.chatbmbis.exceptions;

public class ChatRepeatedException extends ChatException {
    public ChatRepeatedException(String name) {
        super("El chat " + name + " ya existe.", "ChatRepeatedException");
    }
}
