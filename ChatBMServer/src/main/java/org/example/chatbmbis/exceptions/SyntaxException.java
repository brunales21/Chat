package org.example.chatbmbis.exceptions;

public class SyntaxException extends ChatException {
    public SyntaxException(String command) {
        super("El comando " + command + " no existe.", "SyntaxException");
    }
}
