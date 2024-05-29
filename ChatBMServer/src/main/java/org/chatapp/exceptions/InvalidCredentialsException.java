package org.chatapp.exceptions;

public class InvalidCredentialsException extends ChatException {
    public InvalidCredentialsException() {
        super("Credenciales incorrectas.", "wrongCredentials");
    }
}
