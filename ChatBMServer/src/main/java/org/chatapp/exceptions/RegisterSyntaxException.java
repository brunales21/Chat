package org.chatapp.exceptions;

public class RegisterSyntaxException extends ChatException {
    public RegisterSyntaxException(String command) {
        super("La sintaxis de " + command + " es incorrecta.", "comandoNoExiste");
    }
}
