package org.example.chatbmbis;

import java.util.Objects;

public class User {
    private String nickname;
    private ClientType clientType;

    public User(String nickname, ClientType clientType) {
        this.nickname = nickname;
        this.clientType = clientType;
    }

    public User(ClientType clientType) {
        this("", clientType);
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public ClientType getClientType() {
        return clientType;
    }

    public void setClientType(ClientType clientType) {
        this.clientType = clientType;
    }

    public boolean isGUIClient() {
        return clientType.name().equalsIgnoreCase(ClientType.GUI_CLIENT.name());
    }

    public boolean isCLIUser() {
        return clientType.name().equalsIgnoreCase(ClientType.CLI_CLIENT.name());
    }
    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        User client = (User) object;
        return Objects.equals(nickname, client.nickname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nickname);
    }

    @Override
    public String toString() {
        return nickname;
    }
}
