package org.example.chatbmbis.exceptions;

public class RegisterSyntaxException extends RegisterException {
    public RegisterSyntaxException(String command) {
        super("El comando " + command + " no existe.");
    }
}
