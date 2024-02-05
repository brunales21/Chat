package org.example.chatbmbis;

import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mediator {
    private ChatController chatController;
    private AddContactViewController addViewController;
    private ItemContactController itemContactController;
    private LoginController loginController;
    private Map<Stage, Controller> view = new HashMap<>();
    private Map<ItemContactController, String> itemContactControllers = new HashMap<>();
    private List<String> contacts;
    private User user;

    private static Mediator instance;


    public Mediator() {
        this.contacts = new ArrayList<>();
        user = new User();
    }

    public static synchronized Mediator getInstance(User user) {
        if (instance == null) {
            instance = new Mediator();
            instance.setUser(user);
        }
        return instance;
    }


    public void ingresar(String nickname) {
        user.ingresar(nickname);
    }

    public void createChatView() {
        Stage stage = null;
        for (Map.Entry<Stage, Controller> entry : view.entrySet()) {
            if (entry.getKey().getTitle().equals("Chat")) {
                stage = entry.getKey();
                break;
            }
        }
        stage.setResizable(false);
        stage.show();

    }

    public void sendMessage(String messageText) {
        String [] split = Server.splitParts(messageText);
        if (split[0].equals("PRIVMSG")){
            user.sendSaveMessage(messageText);
        }else  {
            user.sendMessage(messageText);
        }
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void createAddView(String promptText, String buttonText) {
        Stage stage = null;
        AddContactViewController addController = null;
        for (Map.Entry<Stage, Controller> entry : view.entrySet()) {
            if (entry.getKey().getTitle().equals("Add")) {
                stage = entry.getKey();
                addController = (AddContactViewController) entry.getValue();
            }
        }
        addController.setPromptText(promptText);
        addController.getAddButton().setText(buttonText);
        stage.setResizable(false);
        stage.show();

    }

    public void createContactItem(String nickname) {
        chatController.createContactItem(nickname);
        //sendMessage("");
    }

/*
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

 */

    public void receiveMessage(String header) {
        String[] headerParts = Server.splitParts(header);
        String senderNickname = headerParts[0];
        String messageText = headerParts[1];
        if (senderNickname.startsWith("#")){

        }else {

        }
        chatController.addMessagesForeingUser(senderNickname + ": " + messageText);
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

    public List<String> getContacts() {
        return contacts;
    }

    public User getUser() {
        return user;
    }
}
