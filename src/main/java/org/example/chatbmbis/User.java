package org.example.chatbmbis;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.*;

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
        this.channels = new ArrayList<>();
        this.privateChats = new ArrayList<>();
    }

    public User(String nickname) {
        this.nickname = nickname;
    }

    public void register(String nickname) {
        setNickname(nickname);
        sendHeader("REGISTER " + nickname);
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
                String header = in.nextLine();
                System.out.println("Header recibido del servidor: "+header);
                String[] headerParts = Server.splitParts(header);
                String senderNickname = headerParts[0];
                String messageText = headerParts[1];
/*
                if (senderNickname.startsWith("#")) {
                    //Channel channel = getChannelByName(senderNickname);

                } else {
                    try {
                        PrivateChat privateChat = getPrivateChatByName(senderNickname);
                        Message message = new Message(privateChat.getUser1(), messageText);
                        privateChat.getMessages().add(message);
                    } catch (IndexOutOfBoundsException errorWindow) {

                    }
                }

 */
                mediator.receiveMessage(header);
            } catch (NoSuchElementException ignored) {

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void sendHeader(String header) {
        PrintStream out = null;
        try {
            out = new PrintStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        out.println(header);

    }

    public String adaptHeader(String header) {
        String[] headerParts = Server.splitParts(header);
        if (headerParts[1].startsWith("#")) {
            header = headerParts[0] + " " + headerParts[1] + " " + nickname + " :" + headerParts[2];
            System.out.println(header);
        } else {
            PrivateChat privateChat = getPrivateChatByName(headerParts[1]);
            Message message1 = new Message(this,headerParts[2]);
            privateChat.getMessages().add(message1);
        }
        return header;
    }

    public PrivateChat getPrivateChatByName(String nickname) {
        return privateChats
                .stream()
                .filter(privateChat1 -> privateChat1.getUser2().getNickname().equals(nickname))
                .toList()
                .get(0);
    }

    public Channel getChannelByName(String nickname) {
        return channels.stream()
                .filter(c -> c.getChannelName().equals(nickname))
                .toList()
                .get(0);
    }


    public void createChannel(String channelName) {
        sendHeader("CREATE #" + channelName);
    }

    //Asociar a controlador de la vista
    public void createPrivateChat(String idUser) {
        sendHeader("CREATE " + idUser);
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

    @Override
    public String toString() {
        return "User{" +
                "nickname='" + nickname + '\'' +
                ", socket=" + socket +
                '}';
    }
}
