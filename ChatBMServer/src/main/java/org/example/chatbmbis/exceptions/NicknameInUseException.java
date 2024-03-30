package org.example.chatbmbis.exceptions;

public class NicknameInUseException extends RegisterException {
    public NicknameInUseException(String name) {
        super("UserExists");
    }
}
