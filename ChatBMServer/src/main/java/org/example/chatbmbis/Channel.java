package org.example.chatbmbis;

import java.util.*;

public class Channel {
    private final String name;
    private final Set<User> users;

    public Channel(String name) {
        this.name = name;
        this.users = new HashSet<>();
    }

    public void addUser(User user) {
        users.add(user);
    }

    public Set<User> getUsers() {
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