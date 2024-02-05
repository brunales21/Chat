package org.example.chatbmbis;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class User extends Client {
    private String nickname;
    private String command;
    private List<Channel> channels;
    private List<PrivateChat> privateChats;
    Mediator mediator;


    public User(String nickname, String hostname, int port) {
        super(hostname, port);
        this.nickname = nickname;
        this.channels = new ArrayList<>();
        this.privateChats = new ArrayList<>();
        try {
            this.socket = new Socket(hostname, port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mediator = Mediator.getInstance(this);
    }

    public User(String hostname, int port) {
        super(hostname, port);
        this.channels = new ArrayList<>();
        this.privateChats = new ArrayList<>();
        try {
            this.socket = new Socket(hostname, port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mediator = Mediator.getInstance(this);
    }

    public User() {
        super();
    }

    public User(String nickname) {
        this.nickname = nickname;
    }

    public void ingresar(String nickname) {
        register(nickname);
        this.start();
    }

    @Override
    public void run() {
        while (!socket.isClosed()) {
            Scanner in = null;
            try {
                in = new Scanner(socket.getInputStream());

                mediator.receiveMessage(in.nextLine());
            } catch (NoSuchElementException ignored) {

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void sendMessage(String message) {
        PrintStream out = null;
        try {
            out = new PrintStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        out.println(message);

    }

    public void register(String nickname) {
        setNickname(nickname);
        sendMessage("REGISTER "+nickname);
    }

    public void createChannel(String channelName) {
        sendMessage("CREATE #" + channelName);
    }

    //Asociar a controlador de la vista
    public void createPrivateChat(String idUser) {
        sendMessage("CREATE " + idUser);
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public void addChatRoom(Channel chatRoom) {
        channels.add(chatRoom);
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }

    public List<PrivateChat> getPrivateChats() {
        return privateChats;
    }

    public void setPrivateChats(List<PrivateChat> privateChats) {
        this.privateChats = privateChats;
    }

    public Mediator getMediator() {
        return mediator;
    }

    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }
}
