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

    public void broadcast(String textMessage) {
        for (User user : users) {
            Socket socket = server.getUserSocketMap().get(user);
            PrintStream out = null;
            try {
                out = new PrintStream(socket.getOutputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            out.println("PRIVMSG #" + channelName + " :" + textMessage);
        }
    }

    public void addUser(User user) {
        users.add(user);
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
}