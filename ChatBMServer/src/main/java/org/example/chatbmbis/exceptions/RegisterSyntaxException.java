package org.example.chatbmbis.exceptions;

public class RegisterSyntaxException extends ChatException {
    public RegisterSyntaxException(String command) {
        super("El comando " + command + " no existe.", "comandoNoExiste");
    }
}
