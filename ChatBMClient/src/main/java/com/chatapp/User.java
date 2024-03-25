package com.chatapp;

import com.chatapp.conversation.Message;
import com.chatapp.mediator.Mediator;
import com.chatapp.utils.WarningWindow;
import javafx.application.Platform;
import com.chatapp.constants.Commands;
import com.chatapp.dao.ChatDAO;
import com.chatapp.dao.FileChatDAO;
import com.chatapp.utils.Utils;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class User extends Client {
    private String nickname;
    private Map<String, List<Message>> chatMessagesMap;
    private final List<String> contacts;
    private final Mediator mediator;
    private ChatDAO chatDAO;
    private final String CHATS_FOLDER_NAME = "chats_messages";
    private boolean registered = false;
    private boolean firstTime = true;

    public User(String nickname, String hostname, int port) {
        super(hostname, port);
        this.nickname = nickname;
        this.contacts = new ArrayList<>();
        chatMessagesMap = new HashMap<>();
        mediator = Mediator.getInstance();
        chatDAO = new FileChatDAO(CHATS_FOLDER_NAME + "/" + this.nickname + "-messages.bin");
    }

    public User() {
        super(DEFAULT_HOSTNAME, DEFAULT_PORT);
        this.nickname = "";
        this.contacts = new ArrayList<>();
        chatMessagesMap = new HashMap<>();
        mediator = Mediator.getInstance();
        chatDAO = new FileChatDAO();
    }

    public User(String nickname) {
        this(nickname, DEFAULT_HOSTNAME, DEFAULT_PORT);
    }

    public void register() {
        sendMessage(Commands.REGISTER.name() + " " + nickname);
    }


    public void ingresar() {
        register();
        if (successfulRegister()) {
            setRegistered(true);
            this.start();
        } else {
            firstTime = false;
            setRegistered(false);
        }
    }

    private boolean successfulRegister() {
        Scanner in = null;
        try {
            in = new Scanner(getSocket().getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String msg;
        if (!firstTime) {
            msg = in.nextLine();
        } else {
            for (int i = 0; i < 3; i++) {
                String thrash = in.nextLine();
            }
            msg = in.nextLine();
        }
        return mediator.isActionApproved(msg);

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
            WarningWindow.instanceWarningWindow("ServidorCaido");
        });
        try {
            getSocket().close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void sendMessage(String commandLine) {
        String[] commandParts = Utils.splitCommandLine(commandLine);
        String command = commandParts[0];
        // si es un mensaje de texto:
        if (command.equalsIgnoreCase(Commands.PRIVMSG.name())) {
            addMessage(commandParts[1], new Message(getNickname(), commandParts[2]));
        }
        PrintStream out = null;
        try {
            out = new PrintStream(getSocket().getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        out.println(commandLine);
    }

    public List<Message> getMessages(String name) {
        List<Message> messages = chatMessagesMap.get(name);
        if (messages == null) {
            messages = new ArrayList<>();
            chatMessagesMap.put(name, messages);
        }
        return messages;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
        ((FileChatDAO) chatDAO).setFile(Path.of(CHATS_FOLDER_NAME + "/" + this.nickname + "-messages.bin"));
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

    public boolean isRegistered() {
        return registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }

    @Override
    public String toString() {
        return "User{" +
                "nickname='" + nickname + '\'' +
                ", socket=" + getSocket() +
                '}';
    }
}