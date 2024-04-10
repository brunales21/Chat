package org.example.chatbmbis.exceptions;

public class SessionAlreadyOpenException extends Exception {
    public SessionAlreadyOpenException(String nickname) {
        super("Ya hay una sesion abierta para el usuario " + nickname + ".");
    }
}
