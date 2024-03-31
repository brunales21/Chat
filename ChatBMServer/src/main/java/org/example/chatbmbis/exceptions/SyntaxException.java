package org.example.chatbmbis.exceptions;

public class SyntaxException extends Exception {
    public SyntaxException(String command) {
        super("El comando " + command + " no existe");
    }
}
