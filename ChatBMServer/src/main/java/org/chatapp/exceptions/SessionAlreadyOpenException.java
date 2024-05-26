package org.chatapp.exceptions;

public class SessionAlreadyOpenException extends ChatException {
    public SessionAlreadyOpenException(String nickname) {
        super("Ya hay una sesion abierta para el usuario " + nickname + ".", "SessionAlreadyOpenException");
    }
}
