package org.chatapp.exceptions;

public class SyntaxException extends ChatException {
    public SyntaxException(String command) {
        super("La sintaxis de " + command + " es incorrecta.", "SyntaxException");
    }

    public SyntaxException(String[] parts) {
        super("La sintaxis de " + String.join(" ", parts) + " no es correcta.", "SyntaxException");
    }

    public SyntaxException() {
        super("Sintaxis de comando incorrecta.", "SyntaxException");
    }
}
