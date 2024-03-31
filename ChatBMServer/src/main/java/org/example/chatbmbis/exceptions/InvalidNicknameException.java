package org.example.chatbmbis.exceptions;

public class InvalidNicknameException extends RegisterException {
    public InvalidNicknameException() {
        super("Intentalo de nuevo con un nombre que no contega [>, <, /, \\]");
    }
}
