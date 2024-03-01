package org.example.chatbmbis.exceptions;

public class UserNotConnectedException extends ChatException {
    public UserNotConnectedException(String arg) {
        super("El usuario " + arg + " no está conectado al servidor.");
    }
}
