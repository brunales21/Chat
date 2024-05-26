package org.chatapp.exceptions;

public class ChatNotFoundException extends ChatException {
    public ChatNotFoundException(String nickname) {
        super("No existe el chat " + nickname + ".", "ChatNotFoundException");
    }

}
