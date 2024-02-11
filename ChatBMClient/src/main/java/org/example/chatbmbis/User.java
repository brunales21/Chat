package org.example.chatbmbis;

import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

public class User extends Client {
    private final String nickname;
    private final Map<String, List<Message>> chatMessagesMap;
    private final Mediator mediator;

    public User(String nickname, String hostname, int port) {
        super(hostname, port);
        this.nickname = nickname;
        chatMessagesMap = new HashMap<>();
        mediator = Mediator.getInstance();
    }

    public void register(String nickname) {
        sendMessage("REGISTER " + nickname);
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
                mediator.receiveMessage(message);
            } catch (NoSuchElementException ignored) {

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void sendMessage(String header) {
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

    public String getNickname() {
        return nickname;
    }

    public Mediator getMediator() {
        return mediator;
    }

    @Override
    public String toString() {
        return "User{" +
                "nickname='" + nickname + '\'' +
                ", socket=" + getSocket() +
                '}';
    }
}