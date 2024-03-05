package org.example.chatbmbis;

import javafx.application.Platform;

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
        this.start();
    }

    public void addMessage(String chatroomName, Message message) {
        getMessages(chatroomName).add(message);
    }

    @Override
    public void run() {
        mediator.getChatController().loadChatItems();
        Scanner in = null;
        try {
            in = new Scanner(getSocket().getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while (getSocket().isConnected()) {
            try {
                System.out.println("1");
                String message = in.nextLine();
                if (message != null) {
                    System.out.println(message);
                    mediator.processServerMsg(message);
                }
            } catch (NoSuchElementException ignored) {
                // Este bloque catch se ejecutará si no hay más líneas para leer, lo que podría indicar una desconexión
                break;  // Salir del bucle al detectar la desconexión
            }
        }

        Platform.runLater(() -> {
            mediator.getChatController().getStage().close();
            ErrorWindow.instanceErrorWindow("ServidorCaido");
        });
        try {
            getSocket().close();
        } catch (IOException e) {
            throw new RuntimeException(e);
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

    public boolean containsContact(String name) {
        return contacts.contains(name);
    }

    public void addContact(String name) {
        contacts.add(name);
    }

    public void removeContact(String nickname) {
        contacts.remove(nickname);
    }
    @Override
    public String toString() {
        return "User{" +
                "nickname='" + nickname + '\'' +
                ", socket=" + getSocket() +
                '}';
    }
}
