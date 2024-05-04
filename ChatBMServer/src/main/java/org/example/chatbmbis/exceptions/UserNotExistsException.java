package org.example.chatbmbis.exceptions;

import org.example.chatbmbis.Channel;

public class UserNotExistsException extends ChatException {
    public UserNotExistsException(String nickname) {
        super("El usuario " + nickname + " no existe.", "UserNotExists");
    }

    public UserNotExistsException() {
        super("El usuario no existe.", "UserNotExists");
    }
}
