package org.example.chatbmbis;

public class ChatNotFoundException extends ChatException {
    private String nickname;
    public ChatNotFoundException(String nickname) {
        super(nickname + " no existe.");
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
