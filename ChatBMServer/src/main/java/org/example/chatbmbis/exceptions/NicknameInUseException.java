package org.example.chatbmbis.exceptions;

public class NicknameInUseException extends ChatException {
    public NicknameInUseException(String name) {
        super("El nickname "+name+"esta en uso.", "UserExists");
    }
}
