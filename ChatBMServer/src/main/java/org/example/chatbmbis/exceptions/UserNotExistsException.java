package org.example.chatbmbis.exceptions;

import org.example.chatbmbis.Channel;

public class UserNotExistsException extends ChatException {
    public UserNotExistsException(String arg) {
        super("UserNotExitsInServer");
    }
}
