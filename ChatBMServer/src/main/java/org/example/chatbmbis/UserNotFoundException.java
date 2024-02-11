package org.example.chatbmbis;

public class UserNotFoundException extends Exception {
    private String nickname;
    public UserNotFoundException(String nickname) {
        super("Usuario " + nickname + " no existe.");
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
