package com.chatapp.model;

import com.chatapp.constants.*;
import com.chatapp.daos.ChatDAO;
import com.chatapp.daos.impl.FileChatDAO;
import com.chatapp.mediator.Mediator;
import com.chatapp.utils.SyntaxUtils;
import com.chatapp.utils.WarningWindow;
import javafx.application.Platform;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.*;

public class User extends Client {

    private final List<String> contacts;
    private final Mediator mediator;
    private final ChatDAO chatDAO;
    private String nickname;
    private Map<String, List<Message>> chatMessagesMap;
    private boolean authenticated = false;

    public User(String nickname, String hostname, int port) {
        super(hostname, port);
        this.nickname = nickname;
        this.contacts = new ArrayList<>();
        chatMessagesMap = new HashMap<>();
        mediator = Mediator.getInstance();
        chatDAO = new FileChatDAO(Constants.CHATS_FOLDER_NAME + "/" + this.nickname + Constants.FILE_SUFIX);
    }

    public User() {
        super();
        this.nickname = "";
        this.contacts = new ArrayList<>();
        chatMessagesMap = new HashMap<>();
        mediator = Mediator.getInstance();
        chatDAO = new FileChatDAO();
    }

    public User(String hostname, int port) {
        super(hostname, port);
        this.nickname = "";
        this.contacts = new ArrayList<>();
        chatMessagesMap = new HashMap<>();
        mediator = Mediator.getInstance();
        chatDAO = new FileChatDAO();
    }

    public User(String nickname) {
        this(nickname, ConnectionConfig.DEFAULT_HOSTNAME, ConnectionConfig.DEFAULT_PORT);
    }

    public void sendLoginCommand() {
        sendMessage(Commands.LOGIN.name() + " " + nickname + " " + mediator.getLoginController().getPasswordField().getText());
    }

    public void sendSignupCommand() {
        sendMessage(Commands.SIGNUP.name() + " " + nickname + " " + mediator.getSignupController().getPasswordField().getText());
    }

    public void sendUserType() {
        sendMessage(ClientType.GUI_CLIENT.name());
    }

    public boolean successfulAuthentication() {
        mediator.filterInput();
        Scanner in = null;
        try {
            in = new Scanner(getSocket().getInputStream());
            String serverResponse = in.nextLine();
            System.out.println(serverResponse);
            return mediator.isActionApproved(serverResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchElementException e) {
            // En caso de que se caiga el servidor en el inicio de sesion
            WarningWindow.instanceWarningWindow(ErrorTypes.SERVER_DOWN);
            return false;
        }
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
                    mediator.processServerMsg(message);
                }
            } catch (NoSuchElementException ignored) {
                // Este bloque catch se ejecutará si no hay más líneas para leer, lo que podría indicar una desconexión
                break;  // Salir del bucle al detectar la desconexión
            }
        }

        Platform.runLater(() -> {
            mediator.getChatController().getStage().close();
            WarningWindow.instanceWarningWindow(ErrorTypes.SERVER_DOWN);
        });
        try {
            getSocket().close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(String commandLine) {
        String[] commandParts = SyntaxUtils.splitCommandLine(commandLine);
        String command = commandParts[0];
        // si es un mensaje de texto:
        if (command.equalsIgnoreCase(Commands.PRIVMSG.name())) {
            addMessage(commandParts[1], new Message(getNickname(), commandParts[2]));
        }
        PrintStream out;
        try {
            out = new PrintStream(getSocket().getOutputStream());
            out.println(commandLine);
        } catch (IOException e) {
            WarningWindow.instanceWarningWindow(ErrorTypes.SERVER_DOWN);
        }
    }



    public List<Message> getMessages(String name) {
        return chatMessagesMap.computeIfAbsent(name, k -> new ArrayList<>());
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
        ((FileChatDAO) chatDAO).setFile(Path.of(Constants.CHATS_FOLDER_NAME + "/" + this.nickname + Constants.FILE_SUFIX));
    }

    public Mediator getMediator() {
        return mediator;
    }

    public Map<String, List<Message>> getChatMessagesMap() {
        return chatMessagesMap;
    }

    public void setChatMessagesMap(Map<String, List<Message>> chatMessagesMap) {
        this.chatMessagesMap = chatMessagesMap;
    }

    public List<String> getContacts() {
        return contacts;
    }

    public ChatDAO getChatDAO() {
        return chatDAO;
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

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    @Override
    public String toString() {
        return "User{" + "nickname='" + nickname + '\'' + ", socket=" + getSocket() + '}';
    }
}
