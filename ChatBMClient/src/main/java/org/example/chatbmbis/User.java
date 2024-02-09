package org.example.chatbmbis;

import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

public class User extends Client {
    private final String nickname;
    private String command;
    private Map<String, List<Message>> chatMessagesMap;
    private List<Channel> channels;
    private List<PrivateChat> privateChats;
    Mediator mediator;


    public User(String nickname, String hostname, int port) {
        super(hostname, port);
        this.nickname = nickname;
        this.channels = new ArrayList<>();
        this.privateChats = new ArrayList<>();
        chatMessagesMap = new HashMap<>();
        mediator = Mediator.getInstance();
    }


    public void register(String nickname) {
        sendHeader("REGISTER " + nickname);
    }

    public void ingresar(String nickname) {
        register(nickname);
        this.start();
    }

    public void addMessage(String chatroomName, Message message) {
        getMessages(chatroomName).add(message);
    }

    @Override
    public void run() {
        while (!getSocket().isClosed()) {
            Scanner in = null;
            try {
                in = new Scanner(getSocket().getInputStream());
                String message = in.nextLine();
                System.out.println("rx: '"+message+"'");
                mediator.receiveMessage(message);
            } catch (NoSuchElementException ignored) {

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void sendHeader(String header) {
        String[] headerParts = Utils.split(header);
        if (headerParts[0].equals("PRIVMSG")) {
            addMessage(headerParts[1], new Message(getNickname(), headerParts[2]));
        }
        PrintStream out = null;
        try {
            out = new PrintStream(getSocket().getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        out.println(header);
    }

    public List<Message> getMessages(String name) {
        List<Message> messages = chatMessagesMap.get(name);
        if (messages == null) {
            messages = new ArrayList<>();
            chatMessagesMap.put(name, messages);
        }
        return messages;
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
                ", socket=" + getSocket() +
                '}';
    }
}
