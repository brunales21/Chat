package org.example.chatbmbis.exceptions;

public class ChatNotFoundException extends ChatException {
    private String nickname;
    public ChatNotFoundException(String nickname) {
        super("ChatNotFoundException");
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
