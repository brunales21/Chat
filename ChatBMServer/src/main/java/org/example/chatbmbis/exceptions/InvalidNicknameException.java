package org.example.chatbmbis.exceptions;

public class InvalidNicknameException extends ChatException {
    public InvalidNicknameException() {
        super("Intentalo de nuevo con un nombre que no contega [>, <, /, \\]", "InvalidNicknameException");
    }
}