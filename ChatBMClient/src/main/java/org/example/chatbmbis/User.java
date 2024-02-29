package org.example.chatbmbis;

import java.io.*;
import java.util.*;

public class User extends Client {
    private final String nickname;
    private Map<String, List<Message>> chatMessagesMap;
    private final List<String> contacts;
    private final Mediator mediator;
    private final ChatDAO chatDAO;

    public User(String nickname, String hostname, int port) throws IOException {
        super(hostname, port);
        this.nickname = nickname;
        this.contacts = new ArrayList<>();
        chatMessagesMap = new HashMap<>();
        mediator = Mediator.getInstance();
        chatDAO = new FileChatDAO(this.nickname + "-messages.bin");
    }

    public void register(String nickname) {
        sendMessage("REGISTER " + nickname);
    }


    public void ingresar(String nickname) {
        register(nickname);
        mediator.getChatController().loadSession();
        this.start();
    }

    public void addMessage(String chatroomName, Message message) {
        getMessages(chatroomName).add(message);
    }

    @Override
    public void run() {
        Scanner in = null;
        try {
            in = new Scanner(getSocket().getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        while (!getSocket().isClosed()) {
            try {
                String message = in.nextLine();
                if (message != null) {
                    mediator.receiveMessage(message);
                }
            } catch (NoSuchElementException ignored) {

            }
        }
    }

    public void sendMessage(String header) {
        String[] headerParts = Utils.split(header);
        String command = headerParts[0];
        // si es un mensaje de texto:
        if (command.equals("PRIVMSG")) {
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

    public void exit() {
        chatDAO.saveChatMessages(mediator.getUser().getChatMessagesMap());
        sendMessage("EXIT");
        try {
            getSocket().close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getNickname() {
        return nickname;
    }

    public Mediator getMediator() {
        return mediator;
    }

    public Map<String, List<Message>> getChatMessagesMap() {
        return chatMessagesMap;
    }

    public List<String> getContacts() {
        return contacts;
    }

    public ChatDAO getChatDAO() {
        return chatDAO;
    }

    public void setChatMessagesMap(Map<String, List<Message>> chatMessagesMap) {
        this.chatMessagesMap = chatMessagesMap;
    }

    @Override
    public String toString() {
        return "User{" +
                "nickname='" + nickname + '\'' +
                ", socket=" + getSocket() +
                '}';
    }
}
