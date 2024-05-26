package org.chatapp.exceptions;

public class SyntaxException extends ChatException {
    public SyntaxException(String command) {
        super("El comando " + command + " no existe.", "SyntaxException");
    }

    public SyntaxException(String[] parts) {
        super("El comando " + String.join(" ", parts) + " no existe.", "SyntaxException");
    }

    public SyntaxException() {
        super("El comando no existe.", "SyntaxException");
    }
}
