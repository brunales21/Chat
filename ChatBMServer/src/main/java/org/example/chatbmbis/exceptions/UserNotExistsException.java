package org.example.chatbmbis.exceptions;

import org.example.chatbmbis.Channel;

public class UserNotExistsException extends ChatException {
    public UserNotExistsException(String arg) {
        super("El usuario " + arg + " no existe en tu lista de contactos. Consulta el comando \"HELP\" si necesitas ayuda.");
    }
}
