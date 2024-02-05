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
        this.channels = new ArrayList<>();
        this.privateChats = new ArrayList<>();
    }

    public User(String nickname) {
        this.nickname = nickname;
    }
    public void register(String nickname) {
        setNickname(nickname);
        sendMessage("REGISTER "+nickname);
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
                String[] headerParts = Server.splitParts(header);
                String senderNickname = headerParts[0];
                String messageText = headerParts[1];

                if (senderNickname.startsWith("#")){
                    Channel channel = channels.stream().filter(channel1 -> channel1.getChannelName().equals(senderNickname)).toList().get(0);
                }else {
                    try {
                        PrivateChat privateChatResult = privateChats.stream().filter(privateChat -> privateChat.getUser2().equals(senderNickname)).toList().get(0);
                        Message message = new Message(privateChatResult.getUser1(), messageText);
                        privateChatResult.getMessages().add(message);
                    }catch (IndexOutOfBoundsException errorWindow){

                    }
                }
                mediator.receiveMessage(header);
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

    public void sendSaveMessage(String message){
        String[] splitParts = Server.splitParts(message);
        if (splitParts[1].startsWith("#")){

        }else {
            PrivateChat privateChat = privateChats.stream().filter(name -> name.getUser2().equals(splitParts[1])).toList().get(0);
            Message message1 = new Message(this,splitParts[2]);
            privateChat.getMessages().add(message1);
        }
        sendMessage(message);
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
