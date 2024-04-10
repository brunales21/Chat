package org.example.chatbmbis.exceptions;

public class InvalidCredentialsException extends ChatException {
    public InvalidCredentialsException() {
        super("Credenciales incorrectas.", "credencialesIncorrectas");
    }
}
