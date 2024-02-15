package org.example.chatbmbis;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Channel {
    private final String name;
    private final List<String> users;

    public Channel(String name) {
        this.name = name;
        this.users = new ArrayList<>();
    }

    public void addUser(String user) {
        users.add(user);
    }

    public List<String> getUsers() {
        return users;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Channel channel = (Channel) o;
        return Objects.equals(name, channel.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}