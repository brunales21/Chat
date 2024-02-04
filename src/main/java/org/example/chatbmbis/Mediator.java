package org.example.chatbmbis;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mediator {
    AddContactViewController addViewController;
    public static ChatController chatController;
    ItemContactController itemContactController;
    LoginController loginController;

    private Map<Stage, Controller> view = new HashMap<>();
    public static Map<ItemContactController, String> itemContactControllers = new HashMap<>();

    private List<String> contacts;

    private User user;

    private static Mediator instance;


    public Mediator() {
        this.contacts = new ArrayList<>();
    }

    public static synchronized Mediator getInstance() {
        if (instance == null) {
            instance = new Mediator();
        }
        return instance;
    }


    public void createChatView(String nickname) {
        Stage stage = null;
        for (Map.Entry<Stage, Controller> entry : view.entrySet()) {
            if (entry.getKey().getTitle().equals("Chat")) {
                stage = entry.getKey();
                //break?
            }
        }

        User user = new User(nickname, "localhost", 80);
        PrintStream out = null;
        try {
            out = new PrintStream(user.getSocket().getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        out.println(nickname);

        stage.setResizable(false);
        stage.show();
        user.start();

    }

    public void sendMessage(String messageText) {
        user.sendMessage(messageText);
    }


    public void createAddView(String promtext, String buttonText) {

        Stage stage = null;
        AddContactViewController addController = null;
        for (Map.Entry<Stage, Controller> entry : view.entrySet()) {
            if (entry.getKey().getTitle().equals("Add")) {
                stage = entry.getKey();
                addController = (AddContactViewController) entry.getValue();
            }
        }
        addController.setPrompText(promtext);
        addController.getAddButton().setText(buttonText);
        stage.setResizable(false);
        stage.show();

    }

    public void createChat(String idButton, String idChat) {
        switch (idButton) {
            case "Crear grupo":
               /* ChatRoom channel = new ChatRoom("#"+idChat);
                User user = (User) server.getUsersMap()
                    .values()
                    .stream()
                    .filter(u -> Objects.equals(u.getNickname(),chatController.getNickName()));


               server.createChannel(channel,user);

                break;
            case "Añadir usuario":
                ChatRoom privateChat = new ChatRoom(idChat);
                /*User user2 = (User) server.getUsersMap()
                        .values()
                        .stream()
                        .filter(u -> Objects.equals(u.getNickname(),chatController.getNickName()));
               // server.createChannel(privateChat,user2);
                createItemChat(idChat);
                break;
                //case "Añadir al grupo":
            */
        }

    }

    public void createItemChat(String nickName) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("contactItemView.fxml"));
        Parent paren = null;
        try {
            paren = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ItemContactController itemContactController1 = loader.getController();
        String name = itemContactController1.getNicknameLabel();

        itemContactController1.setCallback(() -> {
            chatController.setFriendNicknameChatLabelText(name);
        });

        itemContactController1.setNicknameLabelText(name);
        itemContactControllers.put(itemContactController1, nickName);
        contacts.add(nickName);
        chatController.getvBoxPrivate().getChildren().add(paren);

    }

    public void receiveMessage(String header) {
        String[] headerParts = Server.splitParts(header);
        System.out.println(header);
        String senderNickname = headerParts[0];
        String messageText = headerParts[1];
        chatController.addMessagesForeingUser(senderNickname+ ": " +messageText);
    }


    public AddContactViewController getAddViewController() {
        return addViewController;
    }

    public void setAddViewController(AddContactViewController addViewController) {
        this.addViewController = addViewController;
    }

    public ChatController getChatController() {
        return chatController;
    }

    public void setChatController(ChatController chatController) {
        this.chatController = chatController;
    }

    public ItemContactController getItemContactController() {
        return itemContactController;
    }

    public void setItemContactController(ItemContactController itemContactController) {
        this.itemContactController = itemContactController;
    }

    public Map<Stage, Controller> getView() {
        return view;
    }

    public void setView(Map<Stage, Controller> view) {
        this.view = view;
    }

    public static void setInstance(Mediator instance) {
        Mediator.instance = instance;
    }

    public LoginController getLoginController() {
        return loginController;
    }

    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }

    public Map<ItemContactController, String> getItemContactControllers() {
        return itemContactControllers;
    }

    public void setItemContactControllers(Map<ItemContactController, String> itemContactControllers) {
        this.itemContactControllers = itemContactControllers;
    }
}
