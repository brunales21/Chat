package org.example.chatbmbis.exceptions;

public class UserExistsException extends ChatException {
    public UserExistsException(String name) {
        super("Ya existe el usuario " + name + ".");
    }
}
