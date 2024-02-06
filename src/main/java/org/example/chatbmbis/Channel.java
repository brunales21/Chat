package org.example.chatbmbis;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Channel {
    private String channelName;
    private List<User> users;
    private Server server;

    public Channel(String channelName, Server server) {
        this.channelName = channelName;
        this.server = server;
        this.users = new ArrayList<>();
    }

    public Channel(String channelName) {
        this.channelName = channelName;
        this.users = new ArrayList<>();

    }

    public void broadcast(Socket socket1, String textMessage) {
        for (User user : users) {
            Socket socket = Server.getUserSocketMap().get(user);
            if (socket1.equals(socket)) {
                continue;
            }

            PrintStream out = null;
            try {
                out = new PrintStream(socket.getOutputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //#2dam bruno :hola
            out.println(channelName + " " + server.getSocketUserMap().get(socket1).getNickname() +" :" + textMessage);
        }
    }

    public void addUser(User user) {
        users.add(user);
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
}